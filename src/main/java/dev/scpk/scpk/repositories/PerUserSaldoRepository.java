package dev.scpk.scpk.repositories;

import dev.scpk.scpk.dao.PerUserSaldoDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface PerUserSaldoRepository extends CrudRepository<PerUserSaldoDAO, Long> {
}
