package dev.scpk.scpk.hateoas.model.brief;

import dev.scpk.scpk.hateoas.model.full.UserModel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Relation(itemRelation = "briefPaymentGroup", collectionRelation = "briefPaymentGroups")
public class BriefPaymentGroupModel extends RepresentationModel<BriefPaymentGroupModel> {
    private Long id;
    private String name;
    private String description;
    private CollectionModel<UserModel> participants;
    private UserModel owner;
}
