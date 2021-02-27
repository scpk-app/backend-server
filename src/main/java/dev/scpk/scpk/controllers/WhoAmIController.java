package dev.scpk.scpk.controllers;

import dev.scpk.scpk.security.authentication.ExtendedUser;
import dev.scpk.scpk.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/whoami")
public class WhoAmIController {
    @Autowired
    private UserService userService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("")
    public EntityModel<ExtendedUser> get(Authentication authentication){
        this.logger.info("Just checking who am I by user {}", this.userService.getLoggedInUser().toString());
        return EntityModel.of(
                (ExtendedUser) authentication.getPrincipal(),
                WebMvcLinkBuilder.linkTo(WhoAmIController.class).withSelfRel()
        );
    }
}
