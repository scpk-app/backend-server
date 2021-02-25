package dev.scpk.scpk.hateoas.model;

import dev.scpk.scpk.dao.PaymentRequestDAO;
import dev.scpk.scpk.dao.UserBalanceDAO;
import dev.scpk.scpk.dao.UserDAO;
import lombok.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;
import java.util.Set;

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
