package dev.scpk.scpk.dao;

import dev.scpk.scpk.exceptions.security.MissingIdForHashException;
import dev.scpk.scpk.security.acl.SecurityHashable;
import lombok.Data;
import lombok.SneakyThrows;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "PaymentGroup")
@Data
public class PaymentGroupDAO extends DAO implements SecurityHashable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @ManyToMany
    @JoinTable(
            name = "payment_group_participants",
            inverseJoinColumns = @JoinColumn(name = "participant_id"),
            joinColumns = @JoinColumn(name = "payment_group_id")
    )
    private List<UserDAO> participants;

    @ManyToMany
    @JoinTable(
            name = "join_requests_payment_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "payment_group_id")
    )
    private List<UserDAO> requestedToJoin;

    @OneToMany(
            mappedBy = "paymentGroup"
    )
    private List<PaymentRequestDAO> paymentRequests;

    @OneToMany(
            mappedBy = "paymentGroup"
    )
    private List<UserBalanceDAO> userBalances;

    @ManyToOne
    private UserDAO owner;

    private String securityHash;

    public String toString(){
        return String.format(
                "PaymentGroupDAO(id=%s, name=%s, description=%s, owner=%s",
                id.toString(),
                name,
                description,
                owner.getId().toString()
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
