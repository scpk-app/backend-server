package dev.scpk.scpk.exceptions.resolvers;

import dev.scpk.scpk.hateoas.model.full.ErrorModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolationException;

@Component
public class ConstraintViolationExceptionResolver extends AbstractExceptionResolver{
    @Autowired
    private MessageSource messageSource;

    @Override
    public <T extends Exception> ErrorModel resolve(T exception) {
        ConstraintViolationException cve = (ConstraintViolationException) exception;
        return ErrorModel.builder()
                .reason(
                        cve.getConstraintViolations().stream()
//                                .peek(
//                                        constraintViolation -> System.out.println(
//                                                String.format(
//                                                        "constraintDescription=%s, propertyPath=%s",
//                                                            constraintViolation.getConstraintDescriptor(),
//                                                            constraintViolation.getPropertyPath()
//                                                        )
//                                        )
//                                )
                                .map(
                                    a -> this.messageSource.getMessage(
                                            a.getPropertyPath().toString(),
                                            new Object[0],
                                            "empty",
                                            LocaleContextHolder.getLocale()
                                    )
                            ).filter(
                                    s -> !s.equals("empty")
                            ).findFirst().get()
                ).build();
    }

    @Override
    public <T extends Exception> Boolean canResolve(T exception) {
        return exception.getClass().equals(ConstraintViolationException.class);
    }
}
