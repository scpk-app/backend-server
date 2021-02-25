package dev.scpk.scpk.hateoas.assembler;

import dev.scpk.scpk.dao.AuthorityDAO;
import dev.scpk.scpk.hateoas.model.AuthorityModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class AuthorityASM extends RepresentationModelAssemblerSupport<AuthorityDAO, AuthorityModel> {
    public AuthorityASM(Class<?> controllerClass, Class<AuthorityModel> resourceType) {
        super(controllerClass, resourceType);
    }

    public AuthorityASM() {
        super(AuthorityDAO.class, AuthorityModel.class);
    }

    @Override
    public AuthorityModel toModel(AuthorityDAO entity) {
        return AuthorityModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
