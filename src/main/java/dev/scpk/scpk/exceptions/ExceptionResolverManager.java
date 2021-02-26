package dev.scpk.scpk.exceptions;

import dev.scpk.scpk.exceptions.resolvers.AbstractExceptionResolver;
import dev.scpk.scpk.exceptions.resolvers.BindingResultExceptionResolver;
import dev.scpk.scpk.exceptions.resolvers.MissingServletRequestParameterExceptionResolver;
import dev.scpk.scpk.hateoas.model.full.ErrorModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExceptionResolverManager {
    private List<AbstractExceptionResolver> errorResolverList = List.of(
            new BindingResultExceptionResolver(),
            new MissingServletRequestParameterExceptionResolver()
    );

    public <T extends Exception> ErrorModel resolve(T exception){
        for(AbstractExceptionResolver abstractErrorResolver : errorResolverList){
            if(abstractErrorResolver.canResolve(exception))
                return abstractErrorResolver.resolve(exception);
        }
        return new ErrorModel("Exception Unresolvable");
    }
}
