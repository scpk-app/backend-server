package dev.scpk.scpk.services;

import dev.scpk.scpk.dao.DAO;
import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.dao.acl.PermissionDAO;
import dev.scpk.scpk.exceptions.ObjectNotHashableException;
import dev.scpk.scpk.exceptions.UserDoesNotExistsException;
import dev.scpk.scpk.repositories.PermissionRepository;
import dev.scpk.scpk.security.acl.AccessLevel;
import dev.scpk.scpk.security.acl.SecurityHashable;
import dev.scpk.scpk.security.authentication.ExtendedUser;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class ACLService {
    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserService userService;

    private static <T extends DAO> Boolean hasSecurityHash(T object) throws ObjectNotHashableException {
        if(
                Arrays.stream(
                        object.getClass().getInterfaces()
                ).anyMatch(
                        o -> o.isAssignableFrom(SecurityHashable.class)
                )
        ){
            return true;
        }
        else throw new ObjectNotHashableException(object.getClass());
    }

    private static <T extends DAO> void createSecurityHash(T object) throws ObjectNotHashableException {
        ACLService.hasSecurityHash(object);
        Class<?> aClass = object.getClass();
        Long id = object.getId();
        String securityHash = DigestUtils.sha256Hex(aClass.toString() + id.toString());
        ((SecurityHashable) object).setSecurityHash(securityHash);
    }

    private static <T extends DAO> String readSecurityHash(T object) throws ObjectNotHashableException {
        ACLService.hasSecurityHash(object);
        return ((SecurityHashable) object).getSecurityHash();
    }

    private <T extends DAO> PermissionDAO findPermissionByObjectAndUser(T object, ExtendedUser extendedUser) throws ObjectNotHashableException, UserDoesNotExistsException {
        try{
            ACLService.hasSecurityHash(object);
        }
        catch (ObjectNotHashableException ex){
            ACLService.createSecurityHash(object);
        }

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
            case ALL:{
                permissionDAO.setCanWrite(true);
                permissionDAO.setCanRead(true);
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
            case ALL:{
                permissionDAO.setCanWrite(false);
                permissionDAO.setCanRead(false);
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
            case ALL:{
                if(permissionDAO.getCanWrite()){
                    if(permissionDAO.getCanRead()) return true;
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
}
