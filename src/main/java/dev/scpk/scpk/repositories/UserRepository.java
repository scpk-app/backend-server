package dev.scpk.scpk.repositories;

import dev.scpk.scpk.dao.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserDAO, Long> {
     Optional<UserDAO> findByUsername(String username);
}
