package dev.scpk.scpk.repositories;

import dev.scpk.scpk.dao.PaymentRequestDAO;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRequestRepository extends CrudRepository<PaymentRequestDAO, Long> {
}
