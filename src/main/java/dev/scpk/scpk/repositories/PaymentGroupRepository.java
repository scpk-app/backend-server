package dev.scpk.scpk.repositories;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.UserDAO;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PaymentGroupRepository extends CrudRepository<PaymentGroupDAO, Long> {
    List<PaymentGroupDAO> findAllByParticipants(UserDAO userDAO);
}
