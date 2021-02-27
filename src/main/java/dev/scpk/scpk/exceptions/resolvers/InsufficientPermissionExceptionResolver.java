package dev.scpk.scpk.exceptions.resolvers;

import dev.scpk.scpk.exceptions.security.InsufficientPermissionException;
import dev.scpk.scpk.hateoas.model.full.ErrorModel;

public class InsufficientPermissionExceptionResolver extends AbstractExceptionResolver{
    @Override
    public <T extends Exception> ErrorModel resolve(T exception) {
        InsufficientPermissionException ip = (InsufficientPermissionException) exception;
        return ErrorModel.builder()
                .reason(
                        String.format(
                                "User %s do not have %s permission",
                                ip.getExtendedUser().getDisplayName(),
                                ip.getAccessLevel().toString()
                        )
                ).build();
    }

    @Override
    public <T extends Exception> Boolean canResolve(T exception) {
        return exception.getClass().isAssignableFrom(InsufficientPermissionException.class);
    }
}
