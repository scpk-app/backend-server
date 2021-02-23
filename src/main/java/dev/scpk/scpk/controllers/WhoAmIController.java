package dev.scpk.scpk.controllers;

import dev.scpk.scpk.security.authentication.ExtendedUser;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/whoami")
public class WhoAmIController {
    @GetMapping("")
    public EntityModel<ExtendedUser> get(Authentication authentication){
        return EntityModel.of(
                (ExtendedUser) authentication.getPrincipal(),
                WebMvcLinkBuilder.linkTo(WhoAmIController.class).withSelfRel()
        );
    }
}
