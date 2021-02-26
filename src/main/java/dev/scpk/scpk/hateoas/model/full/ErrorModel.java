package dev.scpk.scpk.hateoas.model.full;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
@Relation(itemRelation = "error", collectionRelation = "errors")
public class ErrorModel extends RepresentationModel<ErrorModel> {
    String reason;
}
