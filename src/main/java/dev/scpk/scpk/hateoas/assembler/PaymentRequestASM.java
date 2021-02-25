package dev.scpk.scpk.hateoas.assembler;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.PaymentRequestDAO;
import dev.scpk.scpk.hateoas.model.PaymentRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class PaymentRequestASM extends RepresentationModelAssemblerSupport<PaymentRequestDAO, PaymentRequestModel> {
    @Autowired
    private UserASM userASM;

    public PaymentRequestASM(Class<?> controllerClass, Class<PaymentRequestModel> resourceType) {
        super(controllerClass, resourceType);
    }

    public PaymentRequestASM() {
        super(PaymentGroupDAO.class, PaymentRequestModel.class);
    }

    @Override
    public PaymentRequestModel toModel(PaymentRequestDAO entity) {
        return PaymentRequestModel.builder()
                .id(entity.getId())
                .requestedBy(
                        this.userASM.toModel(entity.getRequestedBy())
                )
                .value(entity.getValue())
                .charged(
                        this.userASM.toCollectionModel(entity.getCharged())
                )
                .build();
    }
}
