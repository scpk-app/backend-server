package dev.scpk.scpk.exceptions.resolvers;

import dev.scpk.scpk.hateoas.model.full.ErrorModel;

public abstract class AbstractExceptionResolver {
    public abstract  <T extends Exception> ErrorModel resolve(T exception);
    public abstract <T extends Exception> Boolean canResolve(T exception);
}
