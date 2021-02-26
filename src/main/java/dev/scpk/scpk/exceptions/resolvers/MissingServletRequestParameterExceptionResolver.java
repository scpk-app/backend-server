package dev.scpk.scpk.exceptions.resolvers;

import dev.scpk.scpk.hateoas.model.full.ErrorModel;
import org.springframework.web.bind.MissingServletRequestParameterException;

public class MissingServletRequestParameterExceptionResolver extends AbstractExceptionResolver{
    @Override
    public <T extends Exception> ErrorModel resolve(T exception) {
        MissingServletRequestParameterException msrp = (MissingServletRequestParameterException) exception;
        return ErrorModel.builder()
                .reason(
                        "Missing " + msrp.getParameterName()
                )
                .build();
    }

    @Override
    public <T extends Exception> Boolean canResolve(T exception) {
        return exception.getClass().isAssignableFrom(MissingServletRequestParameterException.class);
    }
}
