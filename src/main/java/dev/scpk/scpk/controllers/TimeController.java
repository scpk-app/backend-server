package dev.scpk.scpk.controllers;

import dev.scpk.scpk.security.ExtendedUser;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.sql.Time;
import java.time.Instant;

@RestController
@RequestMapping("/whoami")
public class TimeController {
    @GetMapping("")
    public EntityModel<ExtendedUser> get(Authentication authentication){
        return EntityModel.of(
                (ExtendedUser) authentication.getPrincipal(),
                WebMvcLinkBuilder.linkTo(TimeController.class).withSelfRel()
        );
    }
}
