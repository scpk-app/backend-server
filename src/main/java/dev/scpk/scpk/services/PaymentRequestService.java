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
                                    balanceDAO -> chargedUsers.contains(balanceDAO.getUser())
                            ).map(
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
    }

    public void delete(PaymentRequestDAO paymentRequestDAO) throws ObjectNotHashableException, InsufficientPermissionException, UserDoesNotExistsException {
        this.aclService.hasPermissionOrThrowException(paymentRequestDAO, AccessLevel.MODIFY);
        this.paymentRequestRepository.delete(paymentRequestDAO);
    }
}
