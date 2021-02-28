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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserBalanceService {
    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ACLService aclService;

    @Autowired
    private PerUserSaldoService perUserSaldoService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public UserBalanceDAO createUserBalance(UserDAO userDAO, PaymentGroupDAO paymentGroupDAO) throws ObjectNotHashableException, UserDoesNotExistsException {
        this.logger.debug(
                "Creating new user balance for user {} in payment group {}",
                userDAO.toString(),
                paymentGroupDAO.toString()
        );
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
        this.logger.debug(
                "Removin user balance {} in payment group {}",
                userBalanceDAO.toString(),
                paymentGroupDAO.toString()
        );
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
        this.logger.trace(
                "Removed saldo in other user balances"
        );
        paymentGroupDAO.getUserBalances().remove(userBalanceDAO);
        this.userBalanceRepository.delete(userBalanceDAO);
    }
}
