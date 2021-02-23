package dev.scpk.scpk.dao.acl;

import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.security.acl.AccessLevel;
import dev.scpk.scpk.security.authentication.ExtendedUser;
import lombok.Data;

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
    @Column(name = "user")
    private UserDAO user;

    private Boolean canRead;
    private Boolean canWrite;
}
