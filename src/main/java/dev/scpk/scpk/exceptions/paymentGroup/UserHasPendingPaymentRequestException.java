package dev.scpk.scpk.exceptions.paymentGroup;

import dev.scpk.scpk.dao.PaymentRequestDAO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class UserHasPendingPaymentRequestException extends Exception{
    private PaymentRequestDAO paymentRequestDAO;
}
