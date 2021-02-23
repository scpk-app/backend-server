package dev.scpk.scpk.dao;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
@Table(name = "Authority")
@Data
public class AuthorityDAO extends DAO implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @ManyToOne
    private UserDAO user;

    @Override
    public String getAuthority() {
        return this.getName();
    }
}
