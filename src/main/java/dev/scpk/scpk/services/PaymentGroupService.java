package dev.scpk.scpk.services;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.UserBalanceDAO;
import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.exceptions.UserDoesNotExistsException;
import dev.scpk.scpk.repositories.PaymentGroupRepository;
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

    public PaymentGroupDAO createPaymentGroup() throws UserDoesNotExistsException {
        PaymentGroupDAO paymentGroupDAO = new PaymentGroupDAO();
        paymentGroupDAO.setOwner(
                this.userService.convertToUserDAO(
                        this.userService.getLoggedInUser()
                )
        );
        paymentGroupDAO.setParticipants(
                Collections.singleton(
                        this.userService.convertToUserDAO(
                                this.userService.getLoggedInUser()
                        )
                )
        );
        paymentGroupDAO.setUserBalances(
                Collections.singleton(
                        // TODO: create userbalance service to create empty user balance
                )
        );
    }
}
