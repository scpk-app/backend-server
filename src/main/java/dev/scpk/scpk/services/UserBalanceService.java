package dev.scpk.scpk.services;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.UserBalanceDAO;
import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.exceptions.UserDoesNotExistsException;
import dev.scpk.scpk.repositories.UserBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserBalanceService {
    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Autowired
    private UserService userService;

    public UserBalanceDAO createUserBalance(UserDAO userDAO, PaymentGroupDAO paymentGroupDAO){
        UserBalanceDAO userBalanceDAO = new UserBalanceDAO();
        userBalanceDAO.setUser(userDAO);
        userBalanceDAO.setValue(0d);
        userBalanceDAO.setPaymentGroup(paymentGroupDAO);
        return userBalanceDAO;
    }

    public UserBalanceDAO createUserBalance(PaymentGroupDAO paymentGroupDAO) throws UserDoesNotExistsException {
        return this.createUserBalance(
                this.userService.convertToUserDAO(
                    userService.getLoggedInUser()
                ),
                paymentGroupDAO
        );
    }
}
