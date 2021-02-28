package dev.scpk.scpk.exceptions.resolvers;

import dev.scpk.scpk.exceptions.paymentGroup.UserHasPendingPaymentRequestException;
import dev.scpk.scpk.hateoas.model.full.ErrorModel;
import dev.scpk.scpk.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserHasPendingPaymentRequestExceptionResolver extends AbstractExceptionResolver{
    @Autowired
    private UserService userService;

    @Override
    public <T extends Exception> ErrorModel resolve(T exception) {
        UserHasPendingPaymentRequestException uhppr = (UserHasPendingPaymentRequestException) exception;
        return ErrorModel.builder()
                .reason(
                        String.format(
                                "User %s has a pending payment request in group %s so cannot be removed",
                                this.userService.getLoggedInUser().getDisplayName(),
                                uhppr.getPaymentRequestDAO().getPaymentGroup().getName()
                        )
                ).build();
    }

    @Override
    public <T extends Exception> Boolean canResolve(T exception) {
        return exception.getClass().equals(UserHasPendingPaymentRequestException.class);
    }
}
