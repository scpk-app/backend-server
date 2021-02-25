package dev.scpk.scpk.services;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.UserBalanceDAO;
import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.exceptions.security.ObjectNotHashableException;
import dev.scpk.scpk.exceptions.UserDoesNotExistsException;
import dev.scpk.scpk.repositories.UserBalanceRepository;
import dev.scpk.scpk.security.acl.AccessLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserBalanceService {
    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ACLService aclService;

    public UserBalanceDAO createUserBalance(UserDAO userDAO, PaymentGroupDAO paymentGroupDAO) throws ObjectNotHashableException, UserDoesNotExistsException {
        UserBalanceDAO userBalanceDAO = new UserBalanceDAO();
        userBalanceDAO.setUser(userDAO);
        userBalanceDAO.setValue(0d);
        userBalanceDAO.setPaymentGroup(paymentGroupDAO);
        userBalanceDAO = this.userBalanceRepository.save(userBalanceDAO);
        this.aclService.grantPermission(userBalanceDAO, userDAO, AccessLevel.ALL);
        return userBalanceDAO;
    }

    public UserBalanceDAO createUserBalance(PaymentGroupDAO paymentGroupDAO) throws UserDoesNotExistsException, ObjectNotHashableException {
        return this.createUserBalance(
                this.userService.convertToUserDAO(
                    userService.getLoggedInUser()
                ),
                paymentGroupDAO
        );
    }
}
