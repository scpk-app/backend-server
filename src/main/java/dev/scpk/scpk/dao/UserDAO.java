package dev.scpk.scpk.dao;

import dev.scpk.scpk.dao.acl.PermissionDAO;
import dev.scpk.scpk.exceptions.security.MissingIdForHashException;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "User")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDAO extends DAO {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String username;
    @NotNull
    private String displayName;
    @NotNull
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

    @OneToMany(
            mappedBy = "recipient"
    )
    private List<PerUserSaldoDAO> saldos;

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
