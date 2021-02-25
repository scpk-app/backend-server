package dev.scpk.scpk.services;

import dev.scpk.scpk.dao.DAO;
import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.dao.acl.PermissionDAO;
import dev.scpk.scpk.exceptions.security.InsufficientPermissionException;
import dev.scpk.scpk.exceptions.security.ObjectNotHashableException;
import dev.scpk.scpk.exceptions.UserDoesNotExistsException;
import dev.scpk.scpk.repositories.PermissionRepository;
import dev.scpk.scpk.security.acl.AccessLevel;
import dev.scpk.scpk.security.acl.SecurityHashable;
import dev.scpk.scpk.security.authentication.ExtendedUser;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ACLService {
    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserService userService;

    // check if object has a security hash
    // if not create one
    private static <T extends DAO> Boolean hasSecurityHash(T object) throws ObjectNotHashableException {
        if(
                Arrays.stream(
                        object.getClass().getInterfaces()
                ).anyMatch(
                        o -> o.isAssignableFrom(SecurityHashable.class)
                )
        ){
            if(ACLService.readSecurityHash(object) == null)
                ACLService.createSecurityHash(object);
            return true;
        }
        else throw new ObjectNotHashableException(object.getClass());
    }

    // creates security hash based on object class and id
    private static <T extends DAO> void createSecurityHash(T object) throws ObjectNotHashableException {
        Class<?> aClass = object.getClass();
        Long id = object.getId();
        String securityHash = DigestUtils.sha256Hex(aClass.toString() + id.toString());
        ((SecurityHashable) object).setSecurityHash(securityHash);
    }

    private static <T extends DAO> String readSecurityHash(T object) throws ObjectNotHashableException {
        return ((SecurityHashable) object).getSecurityHash();
    }

    private <T extends DAO> PermissionDAO findPermissionByObjectAndUser(T object, ExtendedUser extendedUser) throws ObjectNotHashableException, UserDoesNotExistsException {
        ACLService.hasSecurityHash(object);
        UserDAO userDAO =
                this.userService.convertToUserDAO(
                        extendedUser
                );

        // fetch permission for given user
        Optional<PermissionDAO> permissionDAOOptional =
                this.permissionRepository.findBySecurityHashAndUser(
                        ACLService.readSecurityHash(object),
                        userDAO
                );

        PermissionDAO permissionDAO;
        // permission was never created, as each permission is lazy created
        if(permissionDAOOptional.isEmpty()){
            permissionDAO = new PermissionDAO();
            permissionDAO.setSecurityHash(
                    ACLService.readSecurityHash(object)
            );
            permissionDAO.setUser(userDAO);
            permissionDAO.setCanRead(false);
            permissionDAO.setCanWrite(false);
        }
        else permissionDAO = permissionDAOOptional.get();

        return permissionDAO;
    }

    private <T extends DAO> PermissionDAO findPermissionByObject(T object) throws ObjectNotHashableException, UserDoesNotExistsException {
        return this.findPermissionByObjectAndUser(
                object,
                this.userService.getLoggedInUser()
        );
    }

    public PermissionDAO save(PermissionDAO permissionDAO){
        return this.permissionRepository.save(permissionDAO);
    }

    // grants given permission to user
    // creates new security hash if needed
    public <T extends DAO> void grantPermission(T object, ExtendedUser extendedUser, AccessLevel accessLevel) throws ObjectNotHashableException, UserDoesNotExistsException {
        PermissionDAO permissionDAO = this.findPermissionByObjectAndUser(object, extendedUser);
        switch (accessLevel){
            case READ:{
                permissionDAO.setCanRead(true);
                break;
            }
            case WRITE:{
                permissionDAO.setCanWrite(true);
                break;
            }
            case MODIFY:{
                permissionDAO.setCanModify(true);
                break;
            }
            case ALL:{
                permissionDAO.setCanWrite(true);
                permissionDAO.setCanRead(true);
                permissionDAO.setCanModify(true);
            }
        }

        this.save(permissionDAO);
    }

    public <T extends DAO> void grantPermission(T object, AccessLevel accessLevel) throws ObjectNotHashableException, UserDoesNotExistsException {
        this.grantPermission(
                object,
                this.userService.getLoggedInUser(),
                accessLevel
        );
    }

    public <T extends DAO> void grantPermission(T object, UserDAO userDAO, AccessLevel accessLevel) throws ObjectNotHashableException, UserDoesNotExistsException {
        this.grantPermission(
                object,
                UserService.convertToExtendedUser(userDAO),
                accessLevel
        );
    }

    public <T extends DAO> void grantPermission(Collection<T> objects, UserDAO userDAO, AccessLevel accessLevel) throws ObjectNotHashableException, UserDoesNotExistsException {
        for(T t : objects){
            this.grantPermission(t, userDAO, accessLevel);
        }
    }

    public <T extends DAO> void grantPermission(Collection<T> objects, ExtendedUser extendedUser, AccessLevel accessLevel) throws ObjectNotHashableException, UserDoesNotExistsException {
        for(T t : objects){
            this.grantPermission(t, extendedUser, accessLevel);
        }
    }

    public <T extends DAO> void grantPermission(Collection<T> objects, AccessLevel accessLevel) throws ObjectNotHashableException, UserDoesNotExistsException {
        ExtendedUser loggedInUser = this.userService.getLoggedInUser();
        for(T t : objects){
            this.grantPermission(t, loggedInUser, accessLevel);
        }
    }

    public <T extends DAO> void grantPermission(T object, Collection<UserDAO> users, AccessLevel accessLevel) throws ObjectNotHashableException, UserDoesNotExistsException {
        for(UserDAO user : users){
            this.grantPermission(object, user, accessLevel);
        }
    }

    public <T extends DAO> void grantPermission(T object, Collection<ExtendedUser> users, AccessLevel accessLevel) throws ObjectNotHashableException, UserDoesNotExistsException {
        for(ExtendedUser user : users){
            this.grantPermission(object, user, accessLevel);
        }
    }

    // revoke permission
    // crete hash if needed
    public <T extends DAO> void revokePermission(T object, ExtendedUser extendedUser, AccessLevel accessLevel) throws ObjectNotHashableException, UserDoesNotExistsException {
        PermissionDAO permissionDAO = this.findPermissionByObjectAndUser(object, extendedUser);
        switch (accessLevel){
            case READ:{
                permissionDAO.setCanRead(false);
                break;
            }
            case WRITE:{
                permissionDAO.setCanWrite(false);
                break;
            }
            case MODIFY:{
                permissionDAO.setCanModify(false);
                break;
            }
            case ALL:{
                permissionDAO.setCanWrite(false);
                permissionDAO.setCanRead(false);
                permissionDAO.setCanModify(false);
            }
        }
    }

    public <T extends DAO> void revokePermission(T object, AccessLevel accessLevel) throws ObjectNotHashableException, UserDoesNotExistsException {
        this.revokePermission(
                object,
                this.userService.getLoggedInUser(),
                accessLevel
        );
    }

    // check if user has given permission
    // create security hash if missing
    public <T extends DAO> Boolean hasPermissionTo(T object, ExtendedUser extendedUser, AccessLevel accessLevel) throws ObjectNotHashableException, UserDoesNotExistsException {
        PermissionDAO permissionDAO = this.findPermissionByObjectAndUser(object, extendedUser);
        switch (accessLevel){
            case WRITE:{
                if(permissionDAO.getCanWrite()) return true;
                break;
            }
            case READ:{
                if(permissionDAO.getCanRead()) return true;
                break;
            }
            case MODIFY:{
                if(permissionDAO.getCanModify()) return true;
                break;
            }
            case ALL:{
                if(permissionDAO.getCanWrite()){
                    if(permissionDAO.getCanRead()){
                        if(permissionDAO.getCanModify()) return true;
                    }
                }
                break;
            }
        }
        return false;
    }

    public <T extends DAO> Boolean hasPermissionTo(T object, AccessLevel accessLevel) throws ObjectNotHashableException, UserDoesNotExistsException {
        return this.hasPermissionTo(
                object,
                this.userService.getLoggedInUser(),
                accessLevel
        );
    }

    // check if user has permission and throws error if not
    // create security hash if missing
    public <T extends DAO> void hasPermissionOrThrowException(T object, ExtendedUser extendedUser, AccessLevel accessLevel) throws ObjectNotHashableException, UserDoesNotExistsException, InsufficientPermissionException {
        if(
                !this.hasPermissionTo(object, extendedUser, accessLevel)
        ){
            throw new InsufficientPermissionException(
                    object.getClass(),
                    accessLevel,
                    extendedUser
            );
        }
    }

    public <T extends DAO> void hasPermissionOrThrowException(T object, AccessLevel accessLevel) throws UserDoesNotExistsException, InsufficientPermissionException, ObjectNotHashableException {
        this.hasPermissionOrThrowException(object, this.userService.getLoggedInUser(), accessLevel);
    }

    public  <T extends DAO> List<T> filter(Collection<T> collection, AccessLevel accessLevel){
        return this.filter(
                collection,
                accessLevel,
                this.userService.getLoggedInUser()
        );
    }

    public  <T extends DAO> List<T> filter(Collection<T> collection, AccessLevel accessLevel, ExtendedUser extendedUser){
        return collection.stream().filter(o -> {
            try {
                return this.hasPermissionTo(o, extendedUser, accessLevel);
            } catch (ObjectNotHashableException e) {
                e.printStackTrace();
            } catch (UserDoesNotExistsException e) {
                e.printStackTrace();
            }
            return false;
        }).collect(Collectors.toList());
    }
}
