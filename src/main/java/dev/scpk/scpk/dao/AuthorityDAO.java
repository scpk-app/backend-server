package dev.scpk.scpk.dao;

import dev.scpk.scpk.exceptions.security.MissingIdForHashException;
import lombok.Data;
import lombok.SneakyThrows;
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

    public String toString(){
        return String.format(
                "AuthorityDAO(id=%s, name=%s, userId=%s",
                id.toString(),
                name,
                user.getId().toString()
        );
    }

    @SneakyThrows
    public int hashCode(){
        try {
            return id.hashCode();
        }
        catch (NullPointerException ex){
            throw new MissingIdForHashException(this.getClass().getSimpleName());
        }
    }
}
