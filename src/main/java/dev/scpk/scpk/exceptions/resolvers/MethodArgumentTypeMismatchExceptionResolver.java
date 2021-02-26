package dev.scpk.scpk.exceptions.resolvers;

import dev.scpk.scpk.hateoas.model.full.ErrorModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Component

public class MethodArgumentTypeMismatchExceptionResolver extends AbstractExceptionResolver{
    @Autowired
    private MessageSource messageSource;

    @Override
    public <T extends Exception> ErrorModel resolve(T exception) {
        MethodArgumentTypeMismatchException matm = (MethodArgumentTypeMismatchException) exception;
        return ErrorModel.builder()
                .reason(
                        String.format(
                                "Type missmatch on: %s",
                                this.messageSource.getMessage(
                                        "mismatch.on." + matm.getName(),
                                        new Object[0],
                                        "empty",
                                        LocaleContextHolder.getLocale()
                                )
                        )
                ).build();
    }

    @Override
    public <T extends Exception> Boolean canResolve(T exception) {
        return exception.getClass().isAssignableFrom(MethodArgumentTypeMismatchException.class);
    }
}
