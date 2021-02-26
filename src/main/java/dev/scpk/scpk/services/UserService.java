package dev.scpk.scpk.services;

import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.exceptions.security.ObjectNotHashableException;
import dev.scpk.scpk.exceptions.UserDoesNotExistsException;
import dev.scpk.scpk.exceptions.security.UserAlreadyExistsException;
import dev.scpk.scpk.repositories.UserRepository;
import dev.scpk.scpk.security.authentication.ExtendedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ACLService aclService;

    public UserDAO findOne(String username) throws UserDoesNotExistsException {
        Optional<UserDAO> userDAOOptional = this.userRepository.findByUsername(username);
        if(userDAOOptional.isEmpty()) throw new UserDoesNotExistsException(username);
        else return userDAOOptional.get();
    }

    public UserDAO findOne(Long id) throws UserDoesNotExistsException, ObjectNotHashableException {
        Optional<UserDAO> userDAOOptional = this.userRepository.findById(id);
        if(userDAOOptional.isEmpty()) throw new UserDoesNotExistsException(id.toString());
        else{
            // this.aclService.hasPermissionTo(userDAOOptional.get(), AccessLevel.READ);
            return userDAOOptional.get();
        }
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

    private Boolean checkIfUserExists(UserDAO userDAO){
        return this.userRepository.existsByUsername(
                userDAO.getUsername()
        );
    }

    public UserDAO register(UserDAO userDAO) throws UserAlreadyExistsException {
        if(this.checkIfUserExists(userDAO))
            throw new UserAlreadyExistsException();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String password = userDAO.getPassword();
        String encryptedPassword = bCryptPasswordEncoder.encode(password);
        userDAO.setPassword(encryptedPassword);
        userDAO.setEnabled(true);
        return this.userRepository.save(userDAO);
    }
}
