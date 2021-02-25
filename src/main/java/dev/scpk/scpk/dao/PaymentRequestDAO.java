package dev.scpk.scpk.dao;

import dev.scpk.scpk.exceptions.security.MissingIdForHashException;
import dev.scpk.scpk.security.acl.SecurityHashable;
import lombok.Data;
import lombok.SneakyThrows;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "payment_request")
@Data
public class PaymentRequestDAO extends DAO implements SecurityHashable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double value;

    @ManyToOne
    private UserDAO requestedBy;

    @ManyToMany
    @JoinTable(
            name = "payment_request_charged_user",
            joinColumns = @JoinColumn(name = "payment_request_id"),
            inverseJoinColumns = @JoinColumn(name = "charged_user_id")
    )
    private List<UserDAO> charged;

    @ManyToOne
    private PaymentGroupDAO paymentGroup;
    private String securityHash;

    public String toString(){
        return String.format(
                "PaymentRequestDAO(id=%s, value=%s, requestedBy=%s, paymentGroupId=%s",
                id.toString(),
                value.toString(),
                requestedBy.getId().toString(),
                paymentGroup.getId().toString()
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
