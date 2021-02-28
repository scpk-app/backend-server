package dev.scpk.scpk.exceptions.resolvers;

import dev.scpk.scpk.exceptions.BindingResultException;
import dev.scpk.scpk.hateoas.model.full.ErrorModel;

public class BindingResultExceptionResolver extends AbstractExceptionResolver {
    @Override
    public <T extends Exception> ErrorModel resolve(T exception) {
        BindingResultException bre = (BindingResultException) exception;
        return ErrorModel.builder()
                .reason(
                        bre.getReason()
                )
                .build();
    }

    @Override
    public <T extends Exception> Boolean canResolve(T exception) {
        return exception.getClass().equals(BindingResultException.class);
    }
}
