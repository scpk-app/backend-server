package dev.scpk.scpk.dao;

import dev.scpk.scpk.security.acl.SecurityHashable;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "PaymentGroup")
@Data
public class PaymentGroupDAO extends DAO implements SecurityHashable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "payment_group_participants",
            inverseJoinColumns = @JoinColumn(name = "participant_id"),
            joinColumns = @JoinColumn(name = "payment_group_id")
    )
    private Set<UserDAO> participants;

    @OneToMany(
            mappedBy = "paymentGroup"
    )
    private Set<PaymentRequestDAO> paymentRequests;

    @OneToMany(
            mappedBy = "paymentGroup"
    )
    private Set<UserBalanceDAO> userBalances;

    @ManyToOne
    private UserDAO owner;

    private String securityHash;
}
