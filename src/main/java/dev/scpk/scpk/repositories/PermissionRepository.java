package dev.scpk.scpk.repositories;

import dev.scpk.scpk.dao.UserDAO;
import dev.scpk.scpk.dao.acl.PermissionDAO;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PermissionRepository extends CrudRepository<PermissionDAO, Long> {
    Optional<PermissionDAO> findBySecurityHashAndUser(String securityHash, UserDAO user);
}
