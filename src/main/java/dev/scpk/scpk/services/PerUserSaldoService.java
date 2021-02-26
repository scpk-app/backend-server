package dev.scpk.scpk.services;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.PerUserSaldoDAO;
import dev.scpk.scpk.dao.UserBalanceDAO;
import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.exceptions.UserDoesNotExistsException;
import dev.scpk.scpk.exceptions.security.ObjectNotHashableException;
import dev.scpk.scpk.repositories.PerUserSaldoRepository;
import dev.scpk.scpk.security.acl.AccessLevel;
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

    public PerUserSaldoDAO createPerUserSaldo(UserDAO recipient, UserBalanceDAO userBalanceDAO) throws ObjectNotHashableException, UserDoesNotExistsException {
        PerUserSaldoDAO perUserSaldoDAO = new PerUserSaldoDAO();
        perUserSaldoDAO.setValue(0d);
        perUserSaldoDAO.setUserBalance(userBalanceDAO);
        perUserSaldoDAO.setRecipient(recipient);
        perUserSaldoDAO = this.perUserSaldoRepository.save(perUserSaldoDAO);
        this.aclService.grantPermission(perUserSaldoDAO, AccessLevel.ALL);
        return perUserSaldoDAO;
    }

    public List<PerUserSaldoDAO> createPerUserSaldoAndUpdateOthers(UserBalanceDAO userBalanceDAO, PaymentGroupDAO paymentGroupDAO) throws ObjectNotHashableException, UserDoesNotExistsException {
        List<PerUserSaldoDAO> result = new ArrayList<>();
        List<UserDAO> participants = paymentGroupDAO.getParticipants();
        UserDAO newUser = userBalanceDAO.getUser();
        // create new list with PerUserSaldos
        for(UserDAO participant : participants){
            result.add(
                    this.createPerUserSaldo(participant, userBalanceDAO)
            );
        }
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
        return result;
    }

    public void remove(PerUserSaldoDAO perUserSaldoDAO){
        this.perUserSaldoRepository.delete(perUserSaldoDAO);
    }
}
