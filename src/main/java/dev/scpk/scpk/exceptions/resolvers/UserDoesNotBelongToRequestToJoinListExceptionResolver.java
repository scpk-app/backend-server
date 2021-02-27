package dev.scpk.scpk.exceptions.resolvers;

import dev.scpk.scpk.exceptions.paymentGroup.UserDoesNotBelongToRequestToJoinListException;
import dev.scpk.scpk.hateoas.model.full.ErrorModel;

public class UserDoesNotBelongToRequestToJoinListExceptionResolver extends AbstractExceptionResolver{
    @Override
    public <T extends Exception> ErrorModel resolve(T exception) {
        UserDoesNotBelongToRequestToJoinListException udnbtrtjl = (UserDoesNotBelongToRequestToJoinListException) exception;
        return ErrorModel.builder()
                .reason(
                        String.format(
                                "User with id %s does not belong to group with id %s request to join list, thus cannot be" +
                                        "approved",
                                udnbtrtjl.getUserDAO().getId(),
                                udnbtrtjl.getPaymentGroupDAO().getId()
                        )
                ).build();
    }

    @Override
    public <T extends Exception> Boolean canResolve(T exception) {
        return exception.getClass().isAssignableFrom(UserDoesNotBelongToRequestToJoinListException.class);
    }
}
