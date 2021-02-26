package dev.scpk.scpk.hateoas.model.full;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@Relation(itemRelation = "saldo", collectionRelation = "saldos")
public class PerUserSaldoModel extends RepresentationModel<PerUserSaldoModel> {
    private Long id;
    private UserModel recipient;
    private Double value;
}
