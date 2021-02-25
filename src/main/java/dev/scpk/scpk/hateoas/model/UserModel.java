package dev.scpk.scpk.hateoas.model;

import dev.scpk.scpk.dao.AuthorityDAO;
import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.PaymentRequestDAO;
import dev.scpk.scpk.dao.UserBalanceDAO;
import dev.scpk.scpk.dao.acl.PermissionDAO;
import dev.scpk.scpk.exceptions.MissingIdForHashException;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@Relation(itemRelation = "user", collectionRelation = "users")
public class UserModel extends RepresentationModel<UserModel> {
    private Long id;
    private String username;
    private String displayName;
}
