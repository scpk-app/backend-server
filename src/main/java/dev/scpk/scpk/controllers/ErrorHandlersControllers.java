package dev.scpk.scpk.controllers;

import dev.scpk.scpk.exceptions.BindingResultException;
import dev.scpk.scpk.exceptions.ExceptionResolverManager;
import dev.scpk.scpk.hateoas.model.full.ErrorModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
@RestController
public class ErrorHandlersControllers {
    @Autowired
    private ExceptionResolverManager exceptionResolverManager;

    @ExceptionHandler(BindingResultException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorModel handle(BindingResultException ex){
        return this.exceptionResolverManager.resolve(ex);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorModel handle(MissingServletRequestParameterException ex){
        return this.exceptionResolverManager.resolve(ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorModel handle(ConstraintViolationException ex){
        return this.exceptionResolverManager.resolve(ex);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorModel handle(MethodArgumentTypeMismatchException ex){
        return this.exceptionResolverManager.resolve(ex);
    }
}
