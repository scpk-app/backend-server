package dev.scpk.scpk.exceptions.resolvers;

import dev.scpk.scpk.exceptions.security.UserAlreadyExistsException;
import dev.scpk.scpk.hateoas.model.full.ErrorModel;

public class UserAlreadyExistsExceptionResolver extends AbstractExceptionResolver{
    @Override
    public <T extends Exception> ErrorModel resolve(T exception) {
        return ErrorModel.builder()
                .reason(
                        "User with same username exist"
                ).build();
    }

    @Override
    public <T extends Exception> Boolean canResolve(T exception) {
        return exception.getClass().isAssignableFrom(UserAlreadyExistsException.class);
    }
}
