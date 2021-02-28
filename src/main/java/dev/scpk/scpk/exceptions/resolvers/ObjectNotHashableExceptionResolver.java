package dev.scpk.scpk.exceptions.resolvers;

import dev.scpk.scpk.exceptions.security.ObjectNotHashableException;
import dev.scpk.scpk.hateoas.model.full.ErrorModel;

public class ObjectNotHashableExceptionResolver extends AbstractExceptionResolver{
    @Override
    public <T extends Exception> ErrorModel resolve(T exception) {
        ObjectNotHashableException onh = (ObjectNotHashableException) exception;
        return ErrorModel.builder()
                .reason(
                        String.format(
                                "Object %s is not marked as hashable for permission evaluation, add" +
                                        "SecurityHashable interface to it",
                                onh.getAClass().getSimpleName()
                        )
                )
                .build();
    }

    @Override
    public <T extends Exception> Boolean canResolve(T exception) {
        return exception.getClass().equals(ObjectNotHashableException.class);
    }
}
