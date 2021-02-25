package dev.scpk.scpk.controllers;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.exceptions.*;
import dev.scpk.scpk.exceptions.paymentGroup.UserDoesNotBelongToPaymentGroup;
import dev.scpk.scpk.exceptions.paymentGroup.UserHasPendingPaymentRequestException;
import dev.scpk.scpk.exceptions.security.InsufficientPermissionException;
import dev.scpk.scpk.exceptions.security.ObjectNotHashableException;
import dev.scpk.scpk.hateoas.assembler.PaymentGroupASM;
import dev.scpk.scpk.hateoas.model.PaymentGroupModel;
import dev.scpk.scpk.security.acl.AccessLevel;
import dev.scpk.scpk.services.ACLService;
import dev.scpk.scpk.services.PaymentGroupService;
import dev.scpk.scpk.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/paymentGroup")
public class PaymentGroupController {
    @Autowired
    private PaymentGroupService paymentGroupService;

    @Autowired
    private PaymentGroupASM paymentGroupASM;

    @Autowired
    private UserService userService;

    @Autowired
    private ACLService aclService;

    @PostMapping("/create")
    public PaymentGroupModel createPaymentGroup(
            @RequestParam("name") String name,
            @RequestParam("description") String description
    ) throws UserDoesNotExistsException, ObjectNotHashableException {
        PaymentGroupDAO paymentGroupDAO = this.paymentGroupService.createPaymentGroup(name, description);
        return this.paymentGroupASM.toModel(paymentGroupDAO).add(
                WebMvcLinkBuilder.linkTo(PaymentGroupController.class).withSelfRel()
        );
    }

    @GetMapping("/{id}")
    public PaymentGroupModel getOnePaymentGroup(
            @PathVariable("id") PaymentGroupDAO paymentGroupDAO
    ) throws ObjectNotHashableException, UserDoesNotExistsException, InsufficientPermissionException {
        if(this.aclService.hasPermissionTo(paymentGroupDAO, AccessLevel.READ)) {
            return this.paymentGroupASM.toModel(paymentGroupDAO)
                    .add(WebMvcLinkBuilder.linkTo(PaymentGroupController.class).withSelfRel());
        }
        else
            throw new InsufficientPermissionException(
                    PaymentGroupDAO.class,
                    AccessLevel.READ,
                    this.userService.getLoggedInUser()
            );
    }

    @GetMapping("/all/{id}")
    public CollectionModel<PaymentGroupModel> getAll(
            @PathVariable("id") UserDAO userDAO
    ){
        List<PaymentGroupDAO> paymentGroupDAOList =
                this.paymentGroupService.getAllPaymentGroups(userDAO);

        return this.paymentGroupASM
                .toCollectionModel(paymentGroupDAOList)
                .add(
                        WebMvcLinkBuilder.linkTo(PaymentGroupController.class).withSelfRel()
                );
    }

    @GetMapping("/all")
    public CollectionModel<PaymentGroupModel> getAll() throws UserDoesNotExistsException {
        List<PaymentGroupDAO> paymentGroupDAOList =
                this.paymentGroupService.getAllPaymentGroups(
                        this.userService.convertToUserDAO(
                                this.userService.getLoggedInUser()
                        )
                );

        return this.paymentGroupASM
                .toCollectionModel(paymentGroupDAOList)
                .add(
                        WebMvcLinkBuilder.linkTo(PaymentGroupController.class).withSelfRel()
                );
    }

    @PatchMapping("/requestJoin")
    public String requestJoin(
            @RequestParam("payment_group_id") PaymentGroupDAO paymentGroupDAO
    ) throws UserDoesNotExistsException, ObjectNotHashableException {
        PaymentGroupDAO modifiedPaymentGroup =
                this.paymentGroupService.requestJoinPaymentGroup(paymentGroupDAO);
        return " ";
    }

    @PatchMapping("/approve/")
    public PaymentGroupModel joinGroup(
            @RequestParam("payment_group_id") PaymentGroupDAO paymentGroupDAO,
            @RequestParam("user_id") UserDAO userDAO
    ) throws UserDoesNotExistsException, ObjectNotHashableException, InsufficientPermissionException, UserDoesNotBelongToPaymentGroup {
        PaymentGroupDAO paymentGroupDAOModified =
                this.paymentGroupService.approveToPaymentGroup(
                        paymentGroupDAO, userDAO
                );
        return this.paymentGroupASM.toModel(paymentGroupDAOModified)
                .add(WebMvcLinkBuilder.linkTo(PaymentGroupController.class).withSelfRel());
    }

    @PatchMapping("/leave/{id}")
    public String leave(
            @PathVariable("id") PaymentGroupDAO paymentGroupDAO
    ) throws UserDoesNotExistsException, UserHasPendingPaymentRequestException, ObjectNotHashableException, InsufficientPermissionException {
        this.paymentGroupService.leavePaymentGroup(paymentGroupDAO);
        return " ";
    }

    @PatchMapping("/change/name")
    public PaymentGroupModel changeName(
            @RequestParam("payment_group_id") PaymentGroupDAO paymentGroupDAO,
            @RequestParam("new_name") String newName
    ) throws UserDoesNotExistsException, InsufficientPermissionException, ObjectNotHashableException {
        PaymentGroupDAO newPaymentGroup =
                this.paymentGroupService.changeName(paymentGroupDAO, newName);
        return this.paymentGroupASM.toModel(newPaymentGroup)
                .add(WebMvcLinkBuilder.linkTo(PaymentGroupController.class).withSelfRel());
    }

    @PatchMapping("/change/description")
    public PaymentGroupModel changeDescription(
            @RequestParam("payment_group_id") PaymentGroupDAO paymentGroupDAO,
            @RequestParam("new_description") String newDescription
    ) throws UserDoesNotExistsException, InsufficientPermissionException, ObjectNotHashableException {
        PaymentGroupDAO newPaymentGroup =
                this.paymentGroupService.changeDescription(paymentGroupDAO, newDescription);
        return this.paymentGroupASM.toModel(newPaymentGroup)
                .add(WebMvcLinkBuilder.linkTo(PaymentGroupController.class).withSelfRel());
    }
}
