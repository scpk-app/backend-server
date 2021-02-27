package dev.scpk.scpk.controllers;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.exceptions.*;
import dev.scpk.scpk.exceptions.paymentGroup.UserDoesNotBelongToRequestToJoinListException;
import dev.scpk.scpk.exceptions.paymentGroup.UserHasPendingPaymentRequestException;
import dev.scpk.scpk.exceptions.security.InsufficientPermissionException;
import dev.scpk.scpk.exceptions.security.ObjectNotHashableException;
import dev.scpk.scpk.hateoas.assembler.BriefPaymentGroupASM;
import dev.scpk.scpk.hateoas.assembler.PaymentGroupASM;
import dev.scpk.scpk.hateoas.model.brief.BriefPaymentGroupModel;
import dev.scpk.scpk.hateoas.model.full.PaymentGroupModel;
import dev.scpk.scpk.security.acl.AccessLevel;
import dev.scpk.scpk.services.ACLService;
import dev.scpk.scpk.services.PaymentGroupService;
import dev.scpk.scpk.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import javax.xml.validation.Validator;
import java.util.List;

@RestController
@RequestMapping("/paymentGroup")
@Validated
public class PaymentGroupController {
    @Autowired
    private PaymentGroupService paymentGroupService;

    @Autowired
    private PaymentGroupASM paymentGroupASM;

    @Autowired
    private UserService userService;

    @Autowired
    private ACLService aclService;

    @Autowired
    private BriefPaymentGroupASM briefPaymentGroupASM;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/create")
    public PaymentGroupModel createPaymentGroup(
            @NotEmpty @RequestParam("name") String name,
            @NotEmpty @RequestParam("description") String description
    ) throws UserDoesNotExistsException, ObjectNotHashableException {
        this.logger.info("Serving Payment Group Creation | name {} | description {}", name, description);
        PaymentGroupDAO paymentGroupDAO = this.paymentGroupService.createPaymentGroup(name, description);
        return this.paymentGroupASM.toModel(paymentGroupDAO).add(
                WebMvcLinkBuilder.linkTo(PaymentGroupController.class).withSelfRel()
        );
    }

    @GetMapping("/{id}")
    public PaymentGroupModel getOnePaymentGroup(
            @PathVariable("id") PaymentGroupDAO paymentGroupDAO
    ) throws ObjectNotHashableException, UserDoesNotExistsException, InsufficientPermissionException {
        this.logger.info("Serving GET of payment group | payment group {}", paymentGroupDAO.toString());
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
        this.logger.info("Serving GET of all Payment Groups of user {}", userDAO.toString());
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
        this.logger.info("Serving all Payment Groups of User {}", this.userService.getLoggedInUser().toString());
        List<PaymentGroupDAO> paymentGroupDAOList =
                this.paymentGroupService.getAllPaymentGroups();
        return this.paymentGroupASM
                .toCollectionModel(paymentGroupDAOList)
                .add(
                        WebMvcLinkBuilder.linkTo(PaymentGroupController.class).withSelfRel()
                );
    }

    @GetMapping("/all/brief")
    public CollectionModel<BriefPaymentGroupModel> getAllPaymentGroupsBrief() throws UserDoesNotExistsException {
        this.logger.info("Serving Brief list of Payment Groups of user {}", this.userService.getLoggedInUser().toString());
        List<PaymentGroupDAO> paymentGroupDAOList =
                this.paymentGroupService.getAllPaymentGroups();
        return this.briefPaymentGroupASM.toCollectionModel(paymentGroupDAOList)
                .add(WebMvcLinkBuilder.linkTo(PaymentGroupController.class).withSelfRel());
    }

    @PatchMapping("/requestJoin")
    public String requestJoin(
            @NotEmpty @RequestParam("payment_group_id") PaymentGroupDAO paymentGroupDAO
    ) throws UserDoesNotExistsException, ObjectNotHashableException {
        this.logger.info(
                "Serving request to join of user {} to group {}",
                this.userService.getLoggedInUser().toString(),
                paymentGroupDAO.toString()
        );
        PaymentGroupDAO modifiedPaymentGroup =
                this.paymentGroupService.requestJoinPaymentGroup(paymentGroupDAO);
        return " ";
    }

    @PatchMapping("/approve/")
    public PaymentGroupModel joinGroup(
            @NotEmpty @RequestParam("payment_group_id") PaymentGroupDAO paymentGroupDAO,
            @NotEmpty @RequestParam("user_id") UserDAO userDAO
    ) throws UserDoesNotExistsException, ObjectNotHashableException, InsufficientPermissionException, UserDoesNotBelongToRequestToJoinListException {
        this.logger.info(
                "Serving approvement of user {} to group {}",
                userDAO.toString(),
                paymentGroupDAO.toString()
        );
        PaymentGroupDAO paymentGroupDAOModified =
                this.paymentGroupService.approveToPaymentGroup(
                        paymentGroupDAO, userDAO
                );
        return this.paymentGroupASM.toModel(paymentGroupDAOModified)
                .add(WebMvcLinkBuilder.linkTo(PaymentGroupController.class).withSelfRel());
    }

    @PatchMapping("/leave/{payment_group_id}")
    public String leave(
            @PathVariable("payment_group_id") PaymentGroupDAO paymentGroupDAO
    ) throws UserDoesNotExistsException, UserHasPendingPaymentRequestException, ObjectNotHashableException, InsufficientPermissionException {
        this.logger.info(
                "Serving leave of user {} from payment group {}",
                this.userService.getLoggedInUser().toString(),
                paymentGroupDAO.toString()
        );
        this.paymentGroupService.leavePaymentGroup(paymentGroupDAO);
        return " ";
    }

    @PatchMapping("/change/name")
    public PaymentGroupModel changeName(
            @RequestParam("payment_group_id") PaymentGroupDAO paymentGroupDAO,
            @Size(min = 1) @RequestParam("new_name") String newName
    ) throws UserDoesNotExistsException, InsufficientPermissionException, ObjectNotHashableException {
        this.logger.info(
                "Serving change name of group {} to new name {}",
                paymentGroupDAO.toString(),
                newName
        );
        PaymentGroupDAO newPaymentGroup =
                this.paymentGroupService.changeName(paymentGroupDAO, newName);
        return this.paymentGroupASM.toModel(newPaymentGroup)
                .add(WebMvcLinkBuilder.linkTo(PaymentGroupController.class).withSelfRel());
    }

    @PatchMapping("/change/description")
    public PaymentGroupModel changeDescription(
            @RequestParam("payment_group_id") PaymentGroupDAO paymentGroupDAO,
            @Size(min = 1) @RequestParam("new_description") String newDescription
    ) throws UserDoesNotExistsException, InsufficientPermissionException, ObjectNotHashableException {
        this.logger.info(
                "Serving change of name of group {} to new description {}",
                paymentGroupDAO.toString(),
                newDescription
        );
        PaymentGroupDAO newPaymentGroup =
                this.paymentGroupService.changeDescription(paymentGroupDAO, newDescription);
        return this.paymentGroupASM.toModel(newPaymentGroup)
                .add(WebMvcLinkBuilder.linkTo(PaymentGroupController.class).withSelfRel());
    }
}
