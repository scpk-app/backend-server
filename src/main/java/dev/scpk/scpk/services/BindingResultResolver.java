package dev.scpk.scpk.services;

import dev.scpk.scpk.exceptions.BindingResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Arrays;
import java.util.List;

@Service
public class BindingResultResolver {
    @Autowired
    private MessageSource messageSource;

    public void check(BindingResult bindingResult) throws BindingResultException {
        if(bindingResult.hasErrors()){
            List<ObjectError> objectErrorList = bindingResult.getAllErrors();
            // loop through error list
            String errorReason = objectErrorList.stream()
                    .map(
                            // extract codes
                            objectError -> Arrays.stream(objectError.getCodes())
                                                .map(
                                                        // map with message source
                                                        s -> messageSource.getMessage(
                                                                s,
                                                                new Object[0],
                                                                "empty",
                                                                LocaleContextHolder.getLocale()
                                                        )

                                                ).filter(
                                                        // check if result is different than default value
                                                        s -> !s.equals("empty")
                                                ).findFirst().get()
                    ).findFirst().get();
            if(errorReason == null) throw new BindingResultException("Unavailable to translate error code");
            throw new BindingResultException(errorReason);
        }
    }
}
