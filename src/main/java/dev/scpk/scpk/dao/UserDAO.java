package dev.scpk.scpk.dao;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "User")
@Data
public class UserDAO extends DAO {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String displayName;
    private String password;

    private Boolean enabled;

    @OneToMany(mappedBy = "user")
    @Column(name = "authority")
    private Set<AuthorityDAO> authoritySet;
}
