package dev.scpk.scpk.hateoas.assembler;

import dev.scpk.scpk.dao.UserBalanceDAO;
import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.hateoas.model.UserBalanceModel;
import dev.scpk.scpk.hateoas.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class UserBalanceASM extends RepresentationModelAssemblerSupport<UserBalanceDAO, UserBalanceModel> {
    @Autowired
    private UserASM userASM;

    public UserBalanceASM(Class<?> controllerClass, Class<UserBalanceModel> resourceType) {
        super(controllerClass, resourceType);
    }

    public UserBalanceASM() {
        super(UserBalanceDAO.class, UserBalanceModel.class);
    }

    @Override
    public UserBalanceModel toModel(UserBalanceDAO entity) {
        return UserBalanceModel.builder()
                .id(entity.getId())
                .user(
                        this.userASM.toModel(entity.getUser())
                )
                .value(entity.getValue())
                .build();
    }
}
