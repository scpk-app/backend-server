package dev.scpk.scpk.repositories;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import org.springframework.data.repository.CrudRepository;

public interface PaymentGroupRepository extends CrudRepository<PaymentGroupDAO, Long> {
}
