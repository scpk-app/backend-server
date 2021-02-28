package dev.scpk.scpk.exceptions.resolvers;

import dev.scpk.scpk.exceptions.paymentGroup.PaymentGroupDoesNotExistsException;
import dev.scpk.scpk.hateoas.model.full.ErrorModel;

public class PaymentRequestDoesNotExistExceptionResolver extends AbstractExceptionResolver{
    @Override
    public <T extends Exception> ErrorModel resolve(T exception) {
        PaymentGroupDoesNotExistsException pgdne = (PaymentGroupDoesNotExistsException) exception;
        return ErrorModel.builder()
                .reason(
                        String.format(
                                "Payment Group with id %s does not exist",
                                pgdne.getId().toString()
                        )
                ).build();
    }

    @Override
    public <T extends Exception> Boolean canResolve(T exception) {
        return exception.getClass().equals(PaymentGroupDoesNotExistsException.class);
    }
}
