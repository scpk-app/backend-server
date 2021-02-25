package dev.scpk.scpk.dao;

import dev.scpk.scpk.dao.acl.PermissionDAO;
import dev.scpk.scpk.exceptions.MissingIdForHashException;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "User")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDAO extends DAO {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String displayName;
    private String password;

    private Boolean enabled;

    @OneToMany(mappedBy = "user")
    @Column(name = "authority")
    private List<AuthorityDAO> authoritySet;

    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    private List<PermissionDAO> permissions;

    @ManyToMany
    private List<PaymentGroupDAO> paymentGroups;

    @OneToMany(
            mappedBy = "requestedBy",
            fetch = FetchType.LAZY
    )
    private List<PaymentRequestDAO> paymentRequestsMade;

    @ManyToMany
    private List<PaymentRequestDAO> paymentRequestsReceived;

    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    private List<UserBalanceDAO> userBalances;

    @OneToMany(
            mappedBy = "owner"
    )
    private List<PaymentGroupDAO> ownedPaymentGroups;

    public String toString(){
        return String.format(
                "UserDAO(id=%s, username=%s, displayname=%s, password=%s, enabled=%s",
                id.toString(),
                username,
                displayName,
                password,
                enabled.toString()
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
