package dev.scpk.scpk.controllers;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.exceptions.ObjectNotHashableException;
import dev.scpk.scpk.exceptions.UserDoesNotExistsException;
import dev.scpk.scpk.services.PaymentGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paymentGroup")
public class PaymentGroupController {
    @Autowired
    private PaymentGroupService paymentGroupService;

    @PostMapping("/create")
    public EntityModel<PaymentGroupDAO> createPaymentGroup(
            @RequestParam("name") String name,
            @RequestParam("description") String description
    ) throws UserDoesNotExistsException, ObjectNotHashableException {
        PaymentGroupDAO paymentGroupDAO = this.paymentGroupService.createPaymentGroup(name, description);
        return EntityModel.of(
                null,
                WebMvcLinkBuilder.linkTo(PaymentGroupController.class).withSelfRel()
        );
    }
}
