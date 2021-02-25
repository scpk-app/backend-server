package dev.scpk.scpk.hateoas.assembler;

import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.hateoas.model.UserModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class UserASM extends RepresentationModelAssemblerSupport<UserDAO, UserModel> {
    public UserASM(Class<?> controllerClass, Class<UserModel> resourceType) {
        super(controllerClass, resourceType);
    }

    public UserASM() {
        super(UserDAO.class, UserModel.class);
    }

    @Override
    public UserModel toModel(UserDAO entity) {
        return UserModel.builder()
                .id(entity.getId())
                .displayName(entity.getDisplayName())
                .username(entity.getUsername())
                .build();
    }
}
