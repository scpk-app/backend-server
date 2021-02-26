package dev.scpk.scpk.hateoas.assembler;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.hateoas.model.full.PaymentGroupModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class PaymentGroupASM extends RepresentationModelAssemblerSupport<PaymentGroupDAO, PaymentGroupModel> {
    @Autowired
    private PaymentRequestASM paymentRequestASM;

    @Autowired
    private UserASM userASM;

    @Autowired
    private UserBalanceASM userBalanceASM;

    public PaymentGroupASM(Class<?> controllerClass, Class<PaymentGroupModel> resourceType) {
        super(controllerClass, resourceType);
    }

    public PaymentGroupASM(){
        super(PaymentGroupDAO.class, PaymentGroupModel.class);
    }

    @Override
    public PaymentGroupModel toModel(PaymentGroupDAO entity) {
        return PaymentGroupModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .paymentRequests(
                        this.paymentRequestASM.toCollectionModel(
                                entity.getPaymentRequests()
                        )
                )
                .owner(
                        this.userASM.toModel(
                                entity.getOwner()
                        )
                )
                .userBalances(
                        this.userBalanceASM.toCollectionModel(
                                entity.getUserBalances()
                        )
                )
                .participants(
                        this.userASM.toCollectionModel(
                                entity.getParticipants()
                        )
                )
                .requestedToJoin(
                        this.userASM.toCollectionModel(
                            entity.getRequestedToJoin()
                        )
                )
                .build();
    }
}
