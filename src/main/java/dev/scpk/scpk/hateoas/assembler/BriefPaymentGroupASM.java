package dev.scpk.scpk.hateoas.assembler;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.hateoas.model.brief.BriefPaymentGroupModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class BriefPaymentGroupASM extends RepresentationModelAssemblerSupport<PaymentGroupDAO, BriefPaymentGroupModel> {
    @Autowired
    private UserASM userASM;

    public BriefPaymentGroupASM(Class<?> controllerClass, Class<BriefPaymentGroupModel> resourceType) {
        super(controllerClass, resourceType);
    }

    public BriefPaymentGroupASM() {
        super(PaymentGroupDAO.class, BriefPaymentGroupModel.class);
    }

    @Override
    public BriefPaymentGroupModel toModel(PaymentGroupDAO entity) {
        return BriefPaymentGroupModel.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .participants(
                        this.userASM.toCollectionModel(entity.getParticipants())
                )
                .name(entity.getName())
                .owner(
                        this.userASM.toModel(
                                entity.getOwner()
                        )
                )
                .build();
    }
}
