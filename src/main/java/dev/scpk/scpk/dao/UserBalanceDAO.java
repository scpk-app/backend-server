package dev.scpk.scpk.dao;

import dev.scpk.scpk.security.acl.SecurityHashable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "UserBalance")
@Data
public class UserBalanceDAO extends DAO implements SecurityHashable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserDAO user;

    @ManyToOne
    private PaymentGroupDAO paymentGroup;

    private Double value;

    private String securityHash;
}
