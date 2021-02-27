package dev.scpk.scpk.exceptions.resolvers;

import dev.scpk.scpk.exceptions.UserDoesNotExistsException;
import dev.scpk.scpk.hateoas.model.full.ErrorModel;

public class UserDoesNotExistsExceptionResolver extends AbstractExceptionResolver{
    @Override
    public <T extends Exception> ErrorModel resolve(T exception) {
        UserDoesNotExistsException udne = (UserDoesNotExistsException) exception;
        return ErrorModel.builder()
                .reason(
                        String.format(
                                "User with username %s does not exist",
                                udne.getUsername()
                        )
                ).build();
    }

    @Override
    public <T extends Exception> Boolean canResolve(T exception) {
        return exception.getClass().isAssignableFrom(UserDoesNotExistsException.class);
    }
}
