package dev.scpk.scpk.controllers;

import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.exceptions.BindingResultException;
import dev.scpk.scpk.exceptions.security.UserAlreadyExistsException;
import dev.scpk.scpk.hateoas.assembler.UserASM;
import dev.scpk.scpk.hateoas.model.full.UserModel;
import dev.scpk.scpk.services.BindingResultResolver;
import dev.scpk.scpk.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserASM userASM;

    @Autowired
    private BindingResultResolver bindingResultResolver;

    @PostMapping("/register")
    public UserModel register(
            @Valid UserDAO userDAO,
            BindingResult bindingResult
    ) throws UserAlreadyExistsException, BindingResultException {
        this.bindingResultResolver.check(bindingResult);
        UserDAO registeredUser =
            this.userService.register(userDAO);
        return this.userASM
                .toModel(registeredUser)
                .add(WebMvcLinkBuilder.linkTo(UserController.class).withSelfRel());

    }
}
