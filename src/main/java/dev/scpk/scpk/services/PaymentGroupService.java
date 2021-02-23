package dev.scpk.scpk.services;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.UserBalanceDAO;
import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.exceptions.ObjectNotHashableException;
import dev.scpk.scpk.exceptions.UserDoesNotExistsException;
import dev.scpk.scpk.repositories.PaymentGroupRepository;
import dev.scpk.scpk.security.acl.AccessLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;

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
                Collections.singleton(
                        this.userService.convertToUserDAO(
                                this.userService.getLoggedInUser()
                        )
                )
        );
        // first save payment group and then add user balance of owner, as user balance
        // have to specify payment group it belongs to
        paymentGroupDAO = this.paymentGroupRepository.save(paymentGroupDAO);
        // specify payment group
        paymentGroupDAO.setUserBalances(
                Collections.singleton(
                        this.userBalanceService.createUserBalance(paymentGroupDAO)
                )
        );


        // grant all permissions to owner
        this.aclService.grantPermission(paymentGroupDAO, AccessLevel.ALL);

        return this.paymentGroupRepository.save(paymentGroupDAO);
    }
}
