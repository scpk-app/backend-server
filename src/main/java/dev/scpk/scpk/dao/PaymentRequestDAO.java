package dev.scpk.scpk.dao;

import dev.scpk.scpk.security.acl.SecurityHashable;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "payment_request")
@Data
public class PaymentRequestDAO extends DAO implements SecurityHashable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double value;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    private UserDAO requestedBy;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    private PaymentGroupDAO paymentGroup;

    private String securityHash;
}
