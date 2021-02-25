package dev.scpk.scpk.exceptions.paymentGroup;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.UserDAO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class UserDoesNotBelongToPaymentGroup extends Exception{
    private PaymentGroupDAO paymentGroupDAO;
    private UserDAO userDAO;
}
