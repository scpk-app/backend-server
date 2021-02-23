package dev.scpk.scpk.dao;

import dev.scpk.scpk.security.acl.SecurityHashable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "UserBalance")
@Data
public class UserBalanceDAO extends DAO implements SecurityHashable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    private UserDAO user;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    private PaymentGroupDAO paymentGroup;

    private Double value;

    private String securityHash;
}
