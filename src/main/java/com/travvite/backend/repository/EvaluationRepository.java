package com.travvite.backend.repository;

import com.travvite.backend.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByOuvrierId(Long ouvrierId);
    List<Evaluation> findByClientId(Long clientId);
    Boolean existsByReservationId(Long reservationId);

    @Query("SELECT AVG(e.note) FROM Evaluation e WHERE e.ouvrier.id = :ouvrierId")
    Double calculerNoteMoyenne(Long ouvrierId);
    Long countByOuvrierId(Long ouvrierId);
}
