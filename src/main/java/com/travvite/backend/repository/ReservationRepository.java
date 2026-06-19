package com.travvite.backend.repository;

import com.travvite.backend.model.Reservation;
import com.travvite.backend.model.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByClientId(Long clientId);
    List<Reservation> findByOuvrierId(Long ouvrierId);
    List<Reservation> findByClientIdAndStatut(Long clientId, StatutReservation statut);
    List<Reservation> findByOuvrierIdAndStatut(Long ouvrierId, StatutReservation statut);
}
