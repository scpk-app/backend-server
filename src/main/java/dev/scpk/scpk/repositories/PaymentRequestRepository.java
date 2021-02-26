package dev.scpk.scpk.repositories;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.PaymentRequestDAO;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PaymentRequestRepository extends CrudRepository<PaymentRequestDAO, Long> {
    List<PaymentRequestDAO> findAllByPaymentGroup(PaymentGroupDAO paymentGroupDAO);
}
