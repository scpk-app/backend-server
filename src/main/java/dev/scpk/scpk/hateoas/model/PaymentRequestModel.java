package dev.scpk.scpk.hateoas.model;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.UserDAO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@Relation(itemRelation = "paymentRequest", collectionRelation = "paymentRequests")
public class PaymentRequestModel extends RepresentationModel<PaymentRequestModel> {
    private Long id;
    private Double value;
    private UserModel requestedBy;
    private CollectionModel<UserModel> charged;
}
