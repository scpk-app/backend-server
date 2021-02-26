package dev.scpk.scpk.services;

import dev.scpk.scpk.dao.*;
import dev.scpk.scpk.exceptions.PaymentRequestDoesNotExistException;
import dev.scpk.scpk.exceptions.paymentGroup.PaymentGroupDoesNotExistsException;
import dev.scpk.scpk.exceptions.security.InsufficientPermissionException;
import dev.scpk.scpk.exceptions.security.ObjectNotHashableException;
import dev.scpk.scpk.exceptions.UserDoesNotExistsException;
import dev.scpk.scpk.repositories.PaymentRequestRepository;
import dev.scpk.scpk.security.acl.AccessLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentRequestService {
    @Autowired
    private ACLService aclService;

    @Autowired
    private PaymentRequestRepository paymentRequestRepository;

    @Autowired
    private PaymentGroupService paymentGroupService;

    @Autowired
    private UserService userService;

    // called on first appearance, not only to safe object, but also grant all permissions to new owner
    public PaymentRequestDAO create(List<UserDAO> chargedUsers, PaymentGroupDAO paymentGroupDAO, UserDAO requestedBy, Double value) throws ObjectNotHashableException, UserDoesNotExistsException {
        PaymentRequestDAO paymentRequestDAO = new PaymentRequestDAO();
        paymentRequestDAO.setCharged(chargedUsers);
        paymentRequestDAO.setPaymentGroup(paymentGroupDAO);
        paymentRequestDAO.setRequestedBy(requestedBy);
        paymentRequestDAO.setValue(value);
        paymentRequestDAO = this.paymentRequestRepository.save(paymentRequestDAO);
        this.aclService.grantPermission(paymentRequestDAO, AccessLevel.ALL);
        try {
            this.paymentRequestRepository.save(paymentRequestDAO);
        }
        catch (UnsupportedOperationException ex){
            ex.printStackTrace();
        }
        return paymentRequestDAO;
    }


    public PaymentRequestDAO create(List<UserDAO> chargedUsers, PaymentGroupDAO paymentGroupDAO, Double value) throws UserDoesNotExistsException, ObjectNotHashableException {
        return this.create(
                chargedUsers,
                paymentGroupDAO,
                this.userService.convertToUserDAO(
                        this.userService.getLoggedInUser()
                ),
                value
        );
    }

    // only saves, not granting permissions - convention
    public PaymentRequestDAO save(PaymentRequestDAO paymentRequestDAO){
        return this.paymentRequestRepository.save(paymentRequestDAO);
    }

    public PaymentRequestDAO findOneById(Long id) throws PaymentRequestDoesNotExistException {
        // should not evaluate permissions because of usage in components not responsible for permission
        // evaluation
        Optional<PaymentRequestDAO> paymentRequestDAO =
                this.paymentRequestRepository.findById(id);
        if(paymentRequestDAO.isEmpty())
            throw new PaymentRequestDoesNotExistException(id);
        else
            return paymentRequestDAO.get();
    }

    public List<PaymentRequestDAO> getAllRequested(PaymentGroupDAO paymentGroupDAO) throws UserDoesNotExistsException {
        List<PaymentRequestDAO> paymentRequestDAOS = this.findAllByPaymentGroup(paymentGroupDAO);
        UserDAO loggedInUser = this.userService.convertToUserDAO(
                this.userService.getLoggedInUser()
        );
        paymentRequestDAOS = paymentRequestDAOS.stream()
                .filter(
                        paymentRequestDAO -> paymentRequestDAO.getRequestedBy().equals(loggedInUser)
                ).collect(Collectors.toList());
        return this.aclService.filter(paymentRequestDAOS, AccessLevel.READ);
    }

    // fetch payment request and filter by permission
    public List<PaymentRequestDAO> getAllInWhichUserCharged(PaymentGroupDAO paymentGroupDAO) throws UserDoesNotExistsException {
        List<PaymentRequestDAO> paymentRequestDAOS = this.findAllByPaymentGroup(paymentGroupDAO);
        UserDAO loggedInUser = this.userService.convertToUserDAO(
                this.userService.getLoggedInUser()
        );
        paymentRequestDAOS = paymentRequestDAOS.stream()
                .filter(
                        paymentRequestDAO -> !paymentRequestDAO.getRequestedBy().equals(loggedInUser)
                )
                .filter(
                        paymentRequestDAO -> paymentRequestDAO.getCharged().stream()
                                .anyMatch(
                                        userDAO -> userDAO.equals(loggedInUser)
                                )
                ).collect(Collectors.toList());
        return this.aclService.filter(paymentRequestDAOS, AccessLevel.READ);
    }

    // fetch all payment requests but do not evaluate permissions
    public List<PaymentRequestDAO> findAllByPaymentGroup(PaymentGroupDAO paymentGroupDAO) throws UserDoesNotExistsException {
        return this.paymentRequestRepository.findAllByPaymentGroup(paymentGroupDAO);
    }

    public PaymentGroupDAO addPaymentToPaymentGroup(PaymentGroupDAO paymentGroupDAO, PaymentRequestDAO paymentRequestDAO) throws ObjectNotHashableException, InsufficientPermissionException, UserDoesNotExistsException, PaymentGroupDoesNotExistsException {
        paymentGroupDAO = this.paymentGroupService.findById(paymentGroupDAO.getId());
        this.aclService.hasPermissionOrThrowException(paymentGroupDAO, AccessLevel.WRITE);
        paymentGroupDAO.getPaymentRequests().add(paymentRequestDAO);
        this.paymentGroupService.save(paymentGroupDAO);
        this.aclService.grantPermission(
                paymentRequestDAO,
                paymentGroupDAO.getParticipants(),
                AccessLevel.READ
        );
        this.recalculateUserBalances(paymentGroupDAO);
        return this.paymentGroupService.save(paymentGroupDAO);
    }

    // remove payment request and destroy PaymentRequest entity, as free floating PaymentRequest should not exist
    public PaymentGroupDAO removePaymentRequest(PaymentGroupDAO paymentGroupDAO, PaymentRequestDAO paymentRequestDAO) throws ObjectNotHashableException, InsufficientPermissionException, UserDoesNotExistsException {
        this.aclService.hasPermissionOrThrowException(paymentGroupDAO, AccessLevel.WRITE);
        this.aclService.hasPermissionOrThrowException(paymentRequestDAO, AccessLevel.MODIFY);
        paymentGroupDAO.getPaymentRequests().remove(paymentRequestDAO);
        this.recalculateUserBalances(paymentGroupDAO);
        this.delete(paymentRequestDAO);
        return this.paymentGroupService.save(paymentGroupDAO);
    }

    public void recalculateUserBalances(PaymentGroupDAO paymentGroupDAO){
        List<PaymentRequestDAO> paymentRequestDAOS = paymentGroupDAO.getPaymentRequests();
        // zero all per user saldos
        paymentGroupDAO.setUserBalances(
                paymentGroupDAO.getUserBalances().stream().map(
                        balanceDAO -> {
                            List<PerUserSaldoDAO> newPerUserSaldos =
                                    balanceDAO.getSaldos().stream().map(
                                            saldoDAO -> {
                                                saldoDAO.setValue(0d);
                                                return saldoDAO;
                                            }
                                    ).collect(Collectors.toList());
                            balanceDAO.setSaldos(newPerUserSaldos);
                            return balanceDAO;
                        }
                ).collect(Collectors.toList())
        );
        // calculate new per user salds
        for(PaymentRequestDAO paymentRequestDAO : paymentRequestDAOS){
            List<UserDAO> chargedUsers = paymentRequestDAO.getCharged();
            UserDAO requestedBy = paymentRequestDAO.getRequestedBy();
            if(!chargedUsers.contains(requestedBy)) chargedUsers.add(requestedBy);
            Double requestedValue = paymentRequestDAO.getValue();
            Double singleUserCharge = requestedValue / chargedUsers.size();
            List<UserBalanceDAO> userBalanceDAOS  = paymentGroupDAO.getUserBalances();
            userBalanceDAOS =
                    userBalanceDAOS.stream()
                            .filter(
                                    // filter to match only users charged by given payment request
                                    balanceDAO -> chargedUsers.contains(balanceDAO.getUser())
                            ).map(
                                    // find saldo of user requesting payment and add requested value
                                balanceDAO -> {
                                        List<PerUserSaldoDAO> newPerUserSaldos =
                                                balanceDAO.getSaldos().stream().map(
                                                    saldoDAO -> {
                                                        if(saldoDAO.getRecipient().equals(requestedBy)){
                                                            saldoDAO.setValue(
                                                                saldoDAO.getValue() + singleUserCharge
                                                            );
                                                        }
                                                        return saldoDAO;
                                                    }
                                                ).collect(Collectors.toList());
                                        balanceDAO.setSaldos(newPerUserSaldos);
                                        return balanceDAO;
                                    }
                            ).collect(Collectors.toList());
            paymentGroupDAO.setUserBalances(userBalanceDAOS);
        }
        // combine saldos balance for e.g. if two users charges each by equal value then each of them has 0 balance
        List<UserBalanceDAO> userBalanceDAOS =
                paymentGroupDAO.getUserBalances();
        // for each user balance for user a
        for(int userA = 0; userA < userBalanceDAOS.size(); userA++){
            // values user a has to pay to other users
            List<PerUserSaldoDAO> userAPerUserSaldos = userBalanceDAOS.get(userA).getSaldos();
            // user a on its own
            UserDAO userADAO = userBalanceDAOS.get(userA).getUser();
            // check each entry in per user saldos
            for(int paymentRecipient = 0; paymentRecipient < userAPerUserSaldos.size(); paymentRecipient++){
                // value user a must pay to recipient user
                PerUserSaldoDAO recipientPerUserSaldoForUserA = userAPerUserSaldos.get(paymentRecipient);
                // find user balance for user we have to pay / recipient user
                for(int userB = 0; userB < userBalanceDAOS.size(); userB++){
                    if(userA == userB) continue;
                    UserBalanceDAO userBBalance = userBalanceDAOS.get(userB);
                    // if user match
                    if(
                            recipientPerUserSaldoForUserA.getRecipient().equals(
                                    userBBalance.getUser()
                            )
                    ){
                        // find per user saldo of user a in balance of user b
                        List<PerUserSaldoDAO> userBPerUserSaldos =
                                userBBalance.getSaldos();
                        for(int paymentRequester = 0; paymentRequester < userBPerUserSaldos.size(); paymentRequester++){
                            // value user b has to pay user a
                            PerUserSaldoDAO requesterPerUserSaldo = userBPerUserSaldos.get(paymentRequester);
                            // if names match we found saldo of user a in balance of user b
                            if(requesterPerUserSaldo.getRecipient().equals(userADAO)){
                                Double diff = recipientPerUserSaldoForUserA.getValue() - requesterPerUserSaldo.getValue();
                                // if difference is negative this means
                                // user a requested more
                                if(diff < 0){
                                    recipientPerUserSaldoForUserA.setValue(0d);
                                    requesterPerUserSaldo.setValue(Math.abs(diff));
                                }
                                // if difference is positive then user b ordered more money than user a
                                else{
                                    recipientPerUserSaldoForUserA.setValue(Math.abs(diff));
                                    requesterPerUserSaldo.setValue(0d);
                                }
                                userBPerUserSaldos.remove(paymentRequester);
                                userBPerUserSaldos.add(paymentRequester, requesterPerUserSaldo);
                                break;
                            }
                        }
                        userBalanceDAOS.remove(userB);
                        userBalanceDAOS.add(userB, userBBalance);
                        break;
                    }
                }
                userAPerUserSaldos.remove(paymentRecipient);
                userAPerUserSaldos.add(paymentRecipient, recipientPerUserSaldoForUserA);
            }
            userBalanceDAOS.get(userA).setSaldos(userAPerUserSaldos);
        }
        paymentGroupDAO.setUserBalances(userBalanceDAOS);
    }

    public void delete(PaymentRequestDAO paymentRequestDAO) throws ObjectNotHashableException, InsufficientPermissionException, UserDoesNotExistsException {
        this.aclService.hasPermissionOrThrowException(paymentRequestDAO, AccessLevel.MODIFY);
        this.paymentRequestRepository.delete(paymentRequestDAO);
    }
}
