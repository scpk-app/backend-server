package dev.scpk.scpk.dao.acl;

import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.exceptions.MissingIdForHashException;
import dev.scpk.scpk.security.acl.AccessLevel;
import dev.scpk.scpk.security.authentication.ExtendedUser;
import lombok.Data;
import lombok.SneakyThrows;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Permission")
@Data
public class PermissionDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String securityHash;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserDAO user;

    private Boolean canRead;
    private Boolean canWrite;
    private Boolean canModify;

    public String toString(){
        return String.format(
                "PermissionDAO(id=%s, securityHash=%s, userId=%s, canRead=%s, canWrite=%s",
                id.toString(),
                securityHash,
                user.getId().toString(),
                canRead.toString(),
                canWrite.toString()
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
