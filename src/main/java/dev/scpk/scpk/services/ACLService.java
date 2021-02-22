package dev.scpk.scpk.services;

import dev.scpk.scpk.dao.DAO;
import dev.scpk.scpk.exceptions.ObjectNotHashableException;
import dev.scpk.scpk.security.acl.SecurityHashable;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class ACLService {
    public static <T extends DAO> void createSecurityHash(T object) throws ObjectNotHashableException {
        if(
                Arrays.stream(
                    object.getClass().getInterfaces()
                ).anyMatch(
                        o -> o.isAssignableFrom(SecurityHashable.class)
                )
        ){
            Class<?> aClass = object.getClass();
            Long id = object.getId();
            String securityHash = DigestUtils.sha256Hex(aClass.toString() + id.toString());
            ((SecurityHashable) object).setSecurityHash(securityHash);
        }
        else throw new ObjectNotHashableException(object.getClass());
    }
}
