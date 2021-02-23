package dev.scpk.scpk.repositories;

import dev.scpk.scpk.dao.UserBalanceDAO;
import org.springframework.data.repository.CrudRepository;

public interface UserBalanceRepository extends CrudRepository<UserBalanceDAO, Long> {
}
