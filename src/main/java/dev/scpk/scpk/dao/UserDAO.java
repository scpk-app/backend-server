package dev.scpk.scpk.dao;

import dev.scpk.scpk.dao.acl.PermissionDAO;
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

    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    private Set<PermissionDAO> permissions;

    @ManyToMany
    private Set<PaymentGroupDAO> paymentGroups;

    @OneToMany(
            mappedBy = "requestedBy"
    )
    private Set<PaymentRequestDAO> paymentRequests;

    @OneToMany(
            mappedBy = "user"
    )
    private Set<UserBalanceDAO> userBalances;

    @OneToMany(
            mappedBy = "owner"
    )
    private Set<PaymentGroupDAO> ownedPaymentGroups;
}
