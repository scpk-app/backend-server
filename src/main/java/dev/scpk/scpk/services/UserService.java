package dev.scpk.scpk.services;

import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.exceptions.UserDoesNotExistsException;
import dev.scpk.scpk.repositories.UserRepository;
import dev.scpk.scpk.security.ExtendedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDAO findOne(String username) throws UserDoesNotExistsException {
        Optional<UserDAO> userDAOOptional = this.userRepository.findByUsername(username);
        if(userDAOOptional.isEmpty()) throw new UserDoesNotExistsException(username);
        else return userDAOOptional.get();
    }

    public static ExtendedUser convertToExtendedUser(UserDAO userDAO){
        return new ExtendedUser(
                userDAO.getId(),
                userDAO.getUsername(),
                userDAO.getPassword(),
                userDAO.getDisplayName(),
                userDAO.getEnabled(),
                userDAO.getEnabled(),
                userDAO.getEnabled(),
                userDAO.getEnabled(),
                userDAO.getAuthoritySet()
        );
    }
}
