package dev.scpk.scpk.exceptions.resolvers;

import dev.scpk.scpk.exceptions.security.MissingIdForHashException;
import dev.scpk.scpk.hateoas.model.full.ErrorModel;

public class MissingIdForHashExceptionResolver extends AbstractExceptionResolver{
    @Override
    public <T extends Exception> ErrorModel resolve(T exception) {
        MissingIdForHashException mifh = (MissingIdForHashException) exception;
        return ErrorModel.builder()
                .reason(
                        "Object do not have ID yet, persist it before permission evaluation"
                ).build();
    }

    @Override
    public <T extends Exception> Boolean canResolve(T exception) {
        return exception.getClass().isAssignableFrom(MissingIdForHashException.class);
    }
}
