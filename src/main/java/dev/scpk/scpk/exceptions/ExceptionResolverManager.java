package dev.scpk.scpk.exceptions;

import dev.scpk.scpk.exceptions.resolvers.*;
import dev.scpk.scpk.hateoas.model.full.ErrorModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    public void loadErrorResolvers(){
        this.logger.trace("Loading list with error resolvers");
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
        this.logger.debug("An error occurred. Starting to find proper resolver");
        for(AbstractExceptionResolver abstractErrorResolver : errorResolverList){
            this.logger.trace("Checking if resolver {} match", abstractErrorResolver.toString());
            if(abstractErrorResolver.canResolve(exception))
                this.logger.trace("Resolver {} match", abstractErrorResolver.toString());
                return abstractErrorResolver.resolve(exception);
        }
        this.logger.debug("No resolver found, returning empty error model");
        return new ErrorModel("Exception Unresolvable");
    }
}
