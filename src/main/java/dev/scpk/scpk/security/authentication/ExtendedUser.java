package dev.scpk.scpk.security.authentication;

import dev.scpk.scpk.dao.AuthorityDAO;
import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.services.UserService;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

public class ExtendedUser extends User {
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String displayName;

    public ExtendedUser(Long id, String username, String password, String displayName, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.displayName = displayName;
        this.id = id;
    }

    public ExtendedUser(Long id, String username, String password, String displayName, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.displayName = displayName;
        this.id = id;
    }
}
