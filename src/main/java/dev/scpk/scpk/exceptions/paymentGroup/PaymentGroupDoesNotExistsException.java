package dev.scpk.scpk.exceptions.paymentGroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class PaymentGroupDoesNotExistsException extends Exception{
    private Long id;
}
