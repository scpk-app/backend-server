package dev.scpk.scpk.controllers;

import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.hateoas.assembler.UserASM;
import dev.scpk.scpk.hateoas.model.UserModel;
import dev.scpk.scpk.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserASM userASM;

    @PostMapping("/register")
    public UserModel register(
            UserDAO userDAO
    ){
        UserDAO registeredUser =
            this.userService.register(userDAO);
        return this.userASM
                .toModel(registeredUser)
                .add(WebMvcLinkBuilder.linkTo(UserController.class).withSelfRel());

    }
}
