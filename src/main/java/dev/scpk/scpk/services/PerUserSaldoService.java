package dev.scpk.scpk.services;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.PerUserSaldoDAO;
import dev.scpk.scpk.dao.UserBalanceDAO;
import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.exceptions.UserDoesNotExistsException;
import dev.scpk.scpk.exceptions.security.ObjectNotHashableException;
import dev.scpk.scpk.repositories.PerUserSaldoRepository;
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
public class PerUserSaldoService {
    @Autowired
    private PerUserSaldoRepository perUserSaldoRepository;

    @Autowired
    private ACLService aclService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PerUserSaldoDAO createPerUserSaldo(UserDAO recipient, UserBalanceDAO userBalanceDAO) throws ObjectNotHashableException, UserDoesNotExistsException {
        this.logger.debug(
                "Creating Saldo for user {} in user balance list {}",
                recipient.toString(),
                userBalanceDAO.toString()
        );
        PerUserSaldoDAO perUserSaldoDAO = new PerUserSaldoDAO();
        perUserSaldoDAO.setValue(0d);
        perUserSaldoDAO.setUserBalance(userBalanceDAO);
        perUserSaldoDAO.setRecipient(recipient);
        perUserSaldoDAO = this.perUserSaldoRepository.save(perUserSaldoDAO);
        this.aclService.grantPermission(perUserSaldoDAO, AccessLevel.ALL);
        return perUserSaldoDAO;
    }

    public List<PerUserSaldoDAO> createPerUserSaldoAndUpdateOthers(UserBalanceDAO userBalanceDAO, PaymentGroupDAO paymentGroupDAO) throws ObjectNotHashableException, UserDoesNotExistsException {
        this.logger.debug(
                "Creating new Saldo for user {} and in balance {} and updating all other user balances in group {}",
                userBalanceDAO.getUser().toString(),
                userBalanceDAO.toString(),
                paymentGroupDAO.toString()
        );
        List<PerUserSaldoDAO> result = new ArrayList<>();
        List<UserDAO> participants = paymentGroupDAO.getParticipants();
        UserDAO newUser = userBalanceDAO.getUser();
        // create new list with PerUserSaldos
        for(UserDAO participant : participants){
            result.add(
                    this.createPerUserSaldo(participant, userBalanceDAO)
            );
        }
        this.logger.trace(
                "Created new user balance"
        );
        // update other per user saldos
        int balancesNumber = paymentGroupDAO.getUserBalances().size();
        for(int i = 0; i < balancesNumber; i++){
           if(!paymentGroupDAO.getUserBalances().get(i).getUser().equals(newUser)){
               paymentGroupDAO.getUserBalances().get(i).getSaldos().add(
                       this.createPerUserSaldo(
                               newUser,
                               paymentGroupDAO.getUserBalances().get(i)
                       )
               );
           }
        }
        this.logger.trace(
                "Updated other user balances"
        );
        return result;
    }

    public void remove(PerUserSaldoDAO perUserSaldoDAO){
        this.logger.debug(
                "Removing user slado {}",
                perUserSaldoDAO.toString()
        );
        this.perUserSaldoRepository.delete(perUserSaldoDAO);
    }
}
