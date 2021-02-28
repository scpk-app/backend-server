package dev.scpk.scpk.services;

import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.exceptions.security.ObjectNotHashableException;
import dev.scpk.scpk.exceptions.UserDoesNotExistsException;
import dev.scpk.scpk.exceptions.security.UserAlreadyExistsException;
import dev.scpk.scpk.repositories.UserRepository;
import dev.scpk.scpk.security.authentication.ExtendedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public UserDAO findOne(String username) throws UserDoesNotExistsException {
        this.logger.debug(
                "Looking for user {}",
                username
        );
        Optional<UserDAO> userDAOOptional = this.userRepository.findByUsername(username);
        if(userDAOOptional.isEmpty()){
            this.logger.trace("User not found");
            throw new UserDoesNotExistsException(username);
        }
        else{
            this.logger.trace(
                    "User found {}",
                    userDAOOptional.get().toString()
            );
            return userDAOOptional.get();
        }
    }

    public UserDAO findOne(Long id) throws UserDoesNotExistsException, ObjectNotHashableException {
        this.logger.debug(
                "Looking for user with id {}",
                id.toString()
        );
        Optional<UserDAO> userDAOOptional = this.userRepository.findById(id);
        if(userDAOOptional.isEmpty()) throw new UserDoesNotExistsException(id.toString());
        else{
            // this.aclService.hasPermissionTo(userDAOOptional.get(), AccessLevel.READ);
            return userDAOOptional.get();
        }
    }

    public ExtendedUser convertToExtendedUser(UserDAO userDAO){
        this.logger.debug(
                "Converting user dao {} to extended user",
                userDAO.toString()
        );
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
        this.logger.debug(
                "Converting extended user {} to user dao",
                extendedUser.toString()
        );
        Optional<UserDAO> userDAOOptional = this.userRepository.findById(extendedUser.getId());
        if(userDAOOptional.isEmpty()){
            this.logger.trace(
                    "User not found"
            );
            throw new UserDoesNotExistsException(extendedUser.getUsername());
        }

            this.logger.trace(
                    "User {} found",
                    userDAOOptional.get()
            );
            return userDAOOptional.get();
    }

    public ExtendedUser getLoggedInUser(){
        this.logger.debug(
                "Checking logged in user"
        );
        return (ExtendedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private Boolean checkIfUserExists(UserDAO userDAO){
        this.logger.debug(
                "Checking if user {} exists",
                userDAO.toString()
        );
        return this.userRepository.existsByUsername(
                userDAO.getUsername()
        );
    }

    public UserDAO register(UserDAO userDAO) throws UserAlreadyExistsException {
        this.logger.debug(
                "Registering new user"
        );
        if(this.checkIfUserExists(userDAO)){
            this.logger.trace(
                    "User with given user name exist"
            );
            throw new UserAlreadyExistsException();
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String password = userDAO.getPassword();
        String encryptedPassword = bCryptPasswordEncoder.encode(password);
        userDAO.setPassword(encryptedPassword);
        userDAO.setEnabled(true);
        this.logger.trace(
                "User {} saved",
                userDAO.toString()
        );
        return this.userRepository.save(userDAO);
    }
}
