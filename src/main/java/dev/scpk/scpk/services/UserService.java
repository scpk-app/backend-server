package dev.scpk.scpk.services;

import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.exceptions.UserDoesNotExistsException;
import dev.scpk.scpk.repositories.UserRepository;
import dev.scpk.scpk.security.authentication.ExtendedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

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

    public UserDAO convertToUserDAO(ExtendedUser extendedUser) throws UserDoesNotExistsException {
        Optional<UserDAO> userDAOOptional = this.userRepository.findById(extendedUser.getId());
        if(userDAOOptional.isEmpty()) throw new UserDoesNotExistsException(extendedUser.getUsername());
        return userDAOOptional.get();
    }

    public ExtendedUser getLoggedInUser(){
        return (ExtendedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
