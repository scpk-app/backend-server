package dev.scpk.scpk.services;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.PaymentRequestDAO;
import dev.scpk.scpk.dao.UserBalanceDAO;
import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.exceptions.PaymentRequestDoesNotExistException;
import dev.scpk.scpk.exceptions.security.InsufficientPermissionException;
import dev.scpk.scpk.exceptions.security.ObjectNotHashableException;
import dev.scpk.scpk.exceptions.UserDoesNotExistsException;
import dev.scpk.scpk.repositories.PaymentRequestRepository;
import dev.scpk.scpk.security.acl.AccessLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PaymentRequestService {
    @Autowired
    private ACLService aclService;

    @Autowired
    private PaymentRequestRepository paymentRequestRepository;

    @Autowired
    private PaymentGroupService paymentGroupService;

    // called on first appearance, not only to safe object, but also grant all permissions to new owner
    public PaymentRequestDAO create(PaymentRequestDAO paymentRequestDAO) throws ObjectNotHashableException, UserDoesNotExistsException {
        paymentRequestDAO = this.paymentRequestRepository.save(paymentRequestDAO);
        this.aclService.grantPermission(paymentRequestDAO, AccessLevel.ALL);
        return this.paymentRequestRepository.save(paymentRequestDAO);
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

    public PaymentGroupDAO addPaymentToPaymentGroup(PaymentGroupDAO paymentGroupDAO, PaymentRequestDAO paymentRequestDAO) throws ObjectNotHashableException, InsufficientPermissionException, UserDoesNotExistsException {
        this.aclService.hasPermissionOrThrowException(paymentGroupDAO, AccessLevel.WRITE);
        paymentGroupDAO.getPaymentRequests().add(paymentRequestDAO);
        this.paymentGroupService.save(paymentGroupDAO);
        this.aclService.grantPermission(
                paymentRequestDAO,
                paymentGroupDAO.getParticipants(),
                AccessLevel.READ
        );
        return this.paymentGroupService.save(paymentGroupDAO);
    }

    // remove payment request and destroy PaymentRequest entity, as free floating PaymentRequest should not exist
    public PaymentGroupDAO removePaymentRequest(PaymentGroupDAO paymentGroupDAO, PaymentRequestDAO paymentRequestDAO) throws ObjectNotHashableException, InsufficientPermissionException, UserDoesNotExistsException {
        this.aclService.hasPermissionOrThrowException(paymentGroupDAO, AccessLevel.WRITE);
        this.aclService.hasPermissionOrThrowException(paymentRequestDAO, AccessLevel.MODIFY);
        paymentGroupDAO.getPaymentRequests().remove(paymentRequestDAO);
        this.delete(paymentRequestDAO);
        return this.paymentGroupService.save(paymentGroupDAO);
    }

    public PaymentGroupDAO recalculateUserBalances(PaymentGroupDAO paymentGroupDAO){
        List<PaymentRequestDAO> paymentRequestDAOS = paymentGroupDAO.getPaymentRequests();
        for(PaymentRequestDAO paymentRequestDAO : paymentRequestDAOS){
            List<UserDAO> chargedUsers = paymentRequestDAO.getCharged();
            Double requestedValue = paymentRequestDAO.getValue();
            Double singleUserCharge = requestedValue / chargedUsers.size();
            List<UserBalanceDAO> userBalanceDAOS  = paymentGroupDAO.getUserBalances();
            userBalanceDAOS =
                    userBalanceDAOS.stream()
                            .filter(
                                    o -> chargedUsers.contains(o.getUser())
                            ).map(
                                    o -> {
                                    o.setValue(o.getValue() + singleUserCharge);
                                    return o;
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
