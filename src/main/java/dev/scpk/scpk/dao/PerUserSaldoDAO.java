package dev.scpk.scpk.dao;

import dev.scpk.scpk.exceptions.security.MissingIdForHashException;
import dev.scpk.scpk.security.acl.SecurityHashable;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "PerUserSaldo")
public class PerUserSaldoDAO extends DAO implements SecurityHashable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserBalanceDAO userBalance;

    @ManyToOne
    private UserDAO recipient;

    private Double value;
    private String securityHash;

    public String toString(){
        return String.format(
                "PerUserSaldoDAO(id=%s, recipient=%s, value=%s, userBalanceId=%s",
                this.id.toString(),
                this.recipient.getId().toString(),
                this.value.toString(),
                this.userBalance.getId().toString()
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
