package com.travvite.backend.repository;

import com.travvite.backend.model.Ouvrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface OuvrierRepository extends JpaRepository<Ouvrier, Long>,
        JpaSpecificationExecutor<Ouvrier> {
    Optional<Ouvrier> findByUserId(Long userId);
}
