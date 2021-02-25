package dev.scpk.scpk.converters;

import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.services.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

@Component
public class UserPropertyEditor extends PropertyEditorSupport {
    @Autowired
    private UserService userService;

    @Override
    public String getAsText() {
        UserDAO userDAO = (UserDAO) this.getValue();
        return userDAO.getId().toString();
    }

    @SneakyThrows
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        this.setValue(
                this.userService.findOne(
                        Long.valueOf(text)
                )
        );
    }
}
