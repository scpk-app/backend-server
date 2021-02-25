package dev.scpk.scpk.hateoas.model;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.UserDAO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@Relation(itemRelation = "userBalance", collectionRelation = "userBalances")
public class UserBalanceModel extends RepresentationModel<UserBalanceModel> {
    private Long id;
    private UserModel user;
    private Double value;
}
