package dev.scpk.scpk.dao;

import dev.scpk.scpk.exceptions.security.MissingIdForHashException;
import dev.scpk.scpk.security.acl.SecurityHashable;
import lombok.Data;
import lombok.SneakyThrows;

import javax.persistence.*;
import java.util.List;

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

    @OneToMany(
            mappedBy = "userBalance",
            cascade = CascadeType.REMOVE
    )
    private List<PerUserSaldoDAO> saldos;

    private String securityHash;

    public String toString(){
        return String.format(
                "UserBalanceDAO(id=%s, userId=%s, paymentGroupId=%s",
                id.toString(),
                user.getId().toString(),
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
