package dev.scpk.scpk.services;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.PerUserSaldoDAO;
import dev.scpk.scpk.dao.UserBalanceDAO;
import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.exceptions.security.InsufficientPermissionException;
import dev.scpk.scpk.exceptions.security.ObjectNotHashableException;
import dev.scpk.scpk.exceptions.UserDoesNotExistsException;
import dev.scpk.scpk.repositories.UserBalanceRepository;
import dev.scpk.scpk.security.acl.AccessLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserBalanceService {
    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ACLService aclService;

    @Autowired
    private PerUserSaldoService perUserSaldoService;

    public UserBalanceDAO createUserBalance(UserDAO userDAO, PaymentGroupDAO paymentGroupDAO) throws ObjectNotHashableException, UserDoesNotExistsException {
        UserBalanceDAO userBalanceDAO = new UserBalanceDAO();
        userBalanceDAO.setUser(userDAO);
        userBalanceDAO.setSaldos(new ArrayList<>());
        userBalanceDAO.setPaymentGroup(paymentGroupDAO);
        userBalanceDAO = this.userBalanceRepository.save(userBalanceDAO);
        this.aclService.grantPermission(userBalanceDAO, userDAO, AccessLevel.ALL);
        userBalanceDAO.setSaldos(
                this.perUserSaldoService.createPerUserSaldoAndUpdateOthers(
                    userBalanceDAO,
                    paymentGroupDAO
                )
        );
        this.userBalanceRepository.save(userBalanceDAO);
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

    public void remove(UserBalanceDAO userBalanceDAO, PaymentGroupDAO paymentGroupDAO) throws ObjectNotHashableException, InsufficientPermissionException, UserDoesNotExistsException {
        this.aclService.hasPermissionOrThrowException(userBalanceDAO, AccessLevel.MODIFY);
        List<UserBalanceDAO> userBalanceDAOS = paymentGroupDAO.getUserBalances();
        UserDAO userToRemove = userBalanceDAO.getUser();
        for(UserBalanceDAO singleUserBalance : userBalanceDAOS){
            PerUserSaldoDAO perUserSaldoDAO =
                    singleUserBalance.getSaldos().stream()
                            .filter(
                                    o -> o.getRecipient().equals(userToRemove)
                            ).findFirst().get();
            this.perUserSaldoService.remove(perUserSaldoDAO);
        }
        paymentGroupDAO.getUserBalances().remove(userBalanceDAO);
        this.userBalanceRepository.delete(userBalanceDAO);
    }
}
