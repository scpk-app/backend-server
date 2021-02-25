package dev.scpk.scpk.services;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.PaymentRequestDAO;
import dev.scpk.scpk.dao.UserBalanceDAO;
import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.exceptions.*;
import dev.scpk.scpk.repositories.PaymentGroupRepository;
import dev.scpk.scpk.security.acl.AccessLevel;
import dev.scpk.scpk.security.authentication.ExtendedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
public class PaymentGroupService {
    @Autowired
    private PaymentGroupRepository paymentGroupRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ACLService aclService;

    @Autowired
    private UserBalanceService userBalanceService;

    public PaymentGroupDAO createPaymentGroup(String name, String description) throws UserDoesNotExistsException, ObjectNotHashableException {
        // create payment group
        PaymentGroupDAO paymentGroupDAO = new PaymentGroupDAO();
        // add owner
        paymentGroupDAO.setOwner(
                this.userService.convertToUserDAO(
                        this.userService.getLoggedInUser()
                )
        );
        // only participant is owner
        paymentGroupDAO.setParticipants(
                List.of(
                        this.userService.convertToUserDAO(
                                this.userService.getLoggedInUser()
                        )
                )
        );
        paymentGroupDAO.setPaymentRequests(new ArrayList<>());
        paymentGroupDAO.setUserBalances(new ArrayList<>());
        paymentGroupDAO.setRequestedToJoin(new ArrayList<>());
        paymentGroupDAO.setDescription(description);
        paymentGroupDAO.setName(name);
        // first save payment group and then add user balance of owner, as user balance
        // have to specify payment group it belongs to
        paymentGroupDAO = this.paymentGroupRepository.save(paymentGroupDAO);
        // grant all permissions to owner
        this.aclService.grantPermission(paymentGroupDAO, AccessLevel.ALL);
        paymentGroupDAO.getUserBalances().add(
                this.userBalanceService.createUserBalance(paymentGroupDAO)
        );

        try {
            // throws exception that collections are unmuteable, but successfully persists entity
            this.paymentGroupRepository.save(paymentGroupDAO);
        }
        catch (UnsupportedOperationException ex){
            ex.printStackTrace();
        }
        return paymentGroupDAO;
    }

    public List<PaymentGroupDAO> getAllPaymentGroups(UserDAO userDAO){
        List<PaymentGroupDAO> paymentGroupDAOS = this.paymentGroupRepository.findAllByParticipants(userDAO);
        paymentGroupDAOS =
                this.aclService.filter(
                        paymentGroupDAOS,
                        AccessLevel.READ
                );
        return paymentGroupDAOS;
    }

    public PaymentGroupDAO findById(Long id) throws PaymentGroupDoesNotExistsException, ObjectNotHashableException, UserDoesNotExistsException, InsufficientPermissionException {
        // find by id should not check if user has right privileges,as it could be used in PropertyEditor which should not be
        // responsible for permission evaluation
        Optional<PaymentGroupDAO> paymentGroupDAOOptional =
                this.paymentGroupRepository.findById(id);
        if(paymentGroupDAOOptional.isEmpty()) throw new PaymentGroupDoesNotExistsException(id);
        else {
            return paymentGroupDAOOptional.get();
        }
    }

    public PaymentGroupDAO leavePaymentGroup(PaymentGroupDAO paymentGroupDAO) throws UserDoesNotExistsException, UserHasPendingPaymentRequestException, ObjectNotHashableException, InsufficientPermissionException {
        UserDAO leavingUser =
                this.userService.convertToUserDAO(
                        this.userService.getLoggedInUser()
                );
        // does user have enough permissions
        this.aclService.hasPermissionOrThrowException(paymentGroupDAO, AccessLevel.WRITE);
        // check if any payment request was made toward leaving user, if so reject leave,
        // else remove
        List<PaymentRequestDAO> paymentRequests = paymentGroupDAO.getPaymentRequests();
        Optional<PaymentRequestDAO> paymentRequest =
                paymentRequests.stream()
                    .filter(
                            o -> o.getCharged().contains(leavingUser)
                    ).findFirst();
        if(paymentRequest.isEmpty()){
            // remove user balance
            UserBalanceDAO userBalanceDAO =
                    paymentGroupDAO.getUserBalances().stream()
                        .filter(
                                o -> o.getUser().equals(leavingUser)
                        ).findFirst().get();
            paymentGroupDAO.getUserBalances().remove(userBalanceDAO);
            // remove user from participants list
            paymentGroupDAO.getParticipants().remove(leavingUser);
            this.aclService.revokePermission(paymentGroupDAO, AccessLevel.ALL);
        }
        else
            throw new UserHasPendingPaymentRequestException(paymentRequest.get());
        return this.paymentGroupRepository.save(paymentGroupDAO);
    }

    public PaymentGroupDAO requestJoinPaymentGroup(PaymentGroupDAO paymentGroupDAO) throws UserDoesNotExistsException, ObjectNotHashableException {
        UserDAO joiningUser =
                this.userService.convertToUserDAO(
                        this.userService.getLoggedInUser()
                );
        // check if user already requested to join
        List<UserDAO> requestToJoin = paymentGroupDAO.getRequestedToJoin();
        Boolean isNotOnRequestToJoinList = !requestToJoin.contains(joiningUser);
        Boolean isNotParticipant =
                !paymentGroupDAO.getParticipants().contains(joiningUser);
        if(isNotOnRequestToJoinList && isNotParticipant){
            requestToJoin.add(joiningUser);
            paymentGroupDAO.setRequestedToJoin(requestToJoin);
            paymentGroupDAO = this.paymentGroupRepository.save(paymentGroupDAO);
            // this.aclService.grantPermission(paymentGroupDAO, joiningUser, AccessLevel.READ);
        }
        return paymentGroupDAO;
    }

    public PaymentGroupDAO approveToPaymentGroup(PaymentGroupDAO paymentGroupDAO, UserDAO userDAO) throws UserDoesNotExistsException, ObjectNotHashableException, InsufficientPermissionException, UserDoesNotBelongToPaymentGroup {
        // check if user has proper permissions to read and write to group
        ExtendedUser userToAdd = UserService.convertToExtendedUser(userDAO);
        ExtendedUser userAdding = this.userService.getLoggedInUser();
        // user adding can write to group
        this.aclService.hasPermissionOrThrowException(paymentGroupDAO, userAdding, AccessLevel.MODIFY);

        if(paymentGroupDAO.getRequestedToJoin().contains(userDAO)) {
            paymentGroupDAO.getParticipants().add(userDAO);
            UserBalanceDAO userBalanceDAO = this.userBalanceService.createUserBalance(userDAO, paymentGroupDAO);
            paymentGroupDAO.getUserBalances().add(userBalanceDAO);
            paymentGroupDAO.getRequestedToJoin().remove(userDAO);
            this.aclService.grantPermission(paymentGroupDAO, userToAdd, AccessLevel.READ);
            this.aclService.grantPermission(paymentGroupDAO, userToAdd, AccessLevel.WRITE);
            return this.paymentGroupRepository.save(paymentGroupDAO);
        }
        else
            throw new UserDoesNotBelongToPaymentGroup(paymentGroupDAO, userDAO);
    }

    public PaymentGroupDAO changeDescription(PaymentGroupDAO paymentGroupDAO, String newDescription) throws ObjectNotHashableException, InsufficientPermissionException, UserDoesNotExistsException {
        this.aclService.hasPermissionOrThrowException(paymentGroupDAO, AccessLevel.MODIFY);
        paymentGroupDAO.setDescription(newDescription);
        return this.paymentGroupRepository.save(paymentGroupDAO);
    }

    public PaymentGroupDAO changeName(PaymentGroupDAO paymentGroupDAO, String newName) throws ObjectNotHashableException, InsufficientPermissionException, UserDoesNotExistsException {
        this.aclService.hasPermissionOrThrowException(paymentGroupDAO, AccessLevel.MODIFY);
        paymentGroupDAO.setName(newName);
        return this.paymentGroupRepository.save(paymentGroupDAO);
    }
}
