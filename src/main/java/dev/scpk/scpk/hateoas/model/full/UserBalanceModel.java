package dev.scpk.scpk.hateoas.model.full;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@Relation(itemRelation = "userBalance", collectionRelation = "userBalances")
public class UserBalanceModel extends RepresentationModel<UserBalanceModel> {
    private Long id;
    private UserModel user;
    private CollectionModel<PerUserSaldoModel> saldos;
}
