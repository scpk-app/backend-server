package dev.scpk.scpk.exceptions;

import dev.scpk.scpk.exceptions.resolvers.*;
import dev.scpk.scpk.hateoas.model.full.ErrorModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class ExceptionResolverManager {
    @Autowired
    private ConstraintViolationExceptionResolver constraintViolationExceptionResolver;

    @Autowired
    private MethodArgumentTypeMismatchExceptionResolver methodArgumentTypeMismatchExceptionResolver;

    @Autowired
    private UserHasPendingPaymentRequestExceptionResolver userHasPendingPaymentRequestExceptionResolver;

    @PostConstruct
    public void loadErrorResolvers(){
        this.errorResolverList = List.of(
                new BindingResultExceptionResolver(),
                new MissingServletRequestParameterExceptionResolver(),
                this.constraintViolationExceptionResolver,
                this.methodArgumentTypeMismatchExceptionResolver,
                this.userHasPendingPaymentRequestExceptionResolver,
                new InsufficientPermissionExceptionResolver(),
                new MissingIdForHashExceptionResolver(),
                new ObjectNotHashableExceptionResolver(),
                new PaymentRequestDoesNotExistExceptionResolver(),
                new PaymentGroupDoesNotExistsExceptionResolver(),
                new UserAlreadyExistsExceptionResolver(),
                new UserDoesNotExistsExceptionResolver(),
                new UserDoesNotBelongToRequestToJoinListExceptionResolver()
        );
    }

    private List<AbstractExceptionResolver> errorResolverList;

    public <T extends Exception> ErrorModel resolve(T exception){
        for(AbstractExceptionResolver abstractErrorResolver : errorResolverList){
            if(abstractErrorResolver.canResolve(exception))
                return abstractErrorResolver.resolve(exception);
        }
        return new ErrorModel("Exception Unresolvable");
    }
}
