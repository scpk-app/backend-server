package dev.scpk.scpk.controllers;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.PaymentRequestDAO;
import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.exceptions.UserDoesNotExistsException;
import dev.scpk.scpk.exceptions.paymentGroup.PaymentGroupDoesNotExistsException;
import dev.scpk.scpk.exceptions.security.InsufficientPermissionException;
import dev.scpk.scpk.exceptions.security.ObjectNotHashableException;
import dev.scpk.scpk.hateoas.assembler.PaymentGroupASM;
import dev.scpk.scpk.hateoas.model.PaymentGroupModel;
import dev.scpk.scpk.services.PaymentRequestService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/payment")
public class PaymentRequestController {
    @Autowired
    private PaymentRequestService paymentRequestService;

    @Autowired
    private PaymentGroupASM paymentGroupASM;

    @PatchMapping("/add")
    public PaymentGroupModel addPayment(
            @RequestParam("payment_group_id")PaymentGroupDAO paymentGroupDAO,
            @RequestParam("charged_users_id")UserDAO[] chargedUsers,
            @RequestParam("value") Double value
    ) throws UserDoesNotExistsException, ObjectNotHashableException, InsufficientPermissionException, PaymentGroupDoesNotExistsException {
        PaymentRequestDAO paymentRequestDAO = this.paymentRequestService.create(
                Arrays.asList(chargedUsers),
                paymentGroupDAO,
                value
        );
        PaymentGroupDAO paymentGroupDAO1 = this.paymentRequestService.addPaymentToPaymentGroup(paymentGroupDAO, paymentRequestDAO);
        return this.paymentGroupASM.toModel(paymentGroupDAO1)
                .add(WebMvcLinkBuilder.linkTo(PaymentRequestController.class).withSelfRel());
    }

    @PatchMapping("/remove")
    public PaymentGroupModel removePayment(
            @RequestParam("payment_group_id") PaymentGroupDAO paymentGroupDAO,
            @RequestParam("payment_request_id") PaymentRequestDAO paymentRequestDAO
    ) throws UserDoesNotExistsException, InsufficientPermissionException, ObjectNotHashableException {
        PaymentGroupDAO paymentGroupDAO1 =
                this.paymentRequestService.removePaymentRequest(paymentGroupDAO, paymentRequestDAO);
        return this.paymentGroupASM.toModel(paymentGroupDAO1)
                .add(WebMvcLinkBuilder.linkTo(PaymentRequestController.class).withSelfRel());
    }
}
