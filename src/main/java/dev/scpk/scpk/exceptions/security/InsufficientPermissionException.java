package dev.scpk.scpk.exceptions.security;

import dev.scpk.scpk.security.acl.AccessLevel;
import dev.scpk.scpk.security.authentication.ExtendedUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class InsufficientPermissionException extends Exception{
    private Class<?> target;
    private AccessLevel accessLevel;
    private ExtendedUser extendedUser;
}
