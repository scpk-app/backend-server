package dev.scpk.scpk.hateoas.assembler;

import dev.scpk.scpk.dao.PerUserSaldoDAO;
import dev.scpk.scpk.hateoas.model.PerUserSaldoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class PerUserSaldoASM extends RepresentationModelAssemblerSupport<PerUserSaldoDAO, PerUserSaldoModel> {
    @Autowired
    private UserASM userASM;

    public PerUserSaldoASM(Class<?> controllerClass, Class<PerUserSaldoModel> resourceType) {
        super(controllerClass, resourceType);
    }

    public PerUserSaldoASM() {
        super(PerUserSaldoDAO.class, PerUserSaldoModel.class);
    }

    @Override
    public PerUserSaldoModel toModel(PerUserSaldoDAO entity) {
        return PerUserSaldoModel.builder()
                .id(entity.getId())
                .recipient(
                        this.userASM.toModel(
                                entity.getRecipient()
                        )
                )
                .value(entity.getValue())
                .build();
    }
}
