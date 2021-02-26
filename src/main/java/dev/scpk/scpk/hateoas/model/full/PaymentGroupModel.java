package dev.scpk.scpk.hateoas.model.full;

import lombok.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode(callSuper = true)
@Relation(itemRelation = "paymentGroup", collectionRelation = "paymentsGroup")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentGroupModel extends RepresentationModel<PaymentGroupModel> {
    private Long id;
    private String name;
    private String description;
    private CollectionModel<UserModel> participants;
    private CollectionModel<PaymentRequestModel> paymentRequests;
    private CollectionModel<UserBalanceModel> userBalances;
    private CollectionModel<UserModel> requestedToJoin;
    private UserModel owner;
}
