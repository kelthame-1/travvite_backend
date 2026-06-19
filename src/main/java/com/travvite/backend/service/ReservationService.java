package com.travvite.backend.service;

import com.travvite.backend.model.*;
import com.travvite.backend.repository.ClientRepository;
import com.travvite.backend.repository.OuvrierRepository;
import com.travvite.backend.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ClientRepository clientRepository;
    private final OuvrierRepository ouvrierRepository;
    private final NotificationService notificationService;

    // ===== CREER RESERVATION =====
    public Reservation creerReservation(Long clientId, Long ouvrierId,
                                        String description, LocalDate dateService,
                                        LocalTime heureService, Integer nbHeures,
                                        String adresse) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        Ouvrier ouvrier = ouvrierRepository.findById(ouvrierId)
                .orElseThrow(() -> new RuntimeException("Ouvrier introuvable"));

        Double prixTotal = (ouvrier.getPrixJournalier() / 8.0) * nbHeures;

        Reservation reservation = Reservation.builder()
                .client(client)
                .ouvrier(ouvrier)
                .description(description)
                .dateService(dateService)
                .heureService(heureService)
                .nbHeures(nbHeures)
                .adresse(adresse)
                .prixTotal(prixTotal)
                .statut(StatutReservation.EN_ATTENTE)
                .build();

        reservationRepository.save(reservation);

        notificationService.envoyerNotification(
                ouvrier.getUser(),
                "Nouvelle demande",
                "Vous avez reçu une demande de " + client.getNom(),
                TypeNotification.NOUVELLE_COMMANDE
        );

        return reservation;
    }

    // ===== ACCEPTER =====
    public Reservation accepterReservation(Long id) {
        Reservation reservation = getById(id);
        reservation.setStatut(StatutReservation.ACCEPTE);

        Ouvrier ouvrier = reservation.getOuvrier();
        ouvrier.setEstDisponible(false);
        ouvrierRepository.save(ouvrier);

        notificationService.envoyerNotification(
                reservation.getClient().getUser(),
                "Demande acceptée",
                "Votre demande a été acceptée par " +
                        reservation.getOuvrier().getNom(),
                TypeNotification.COMMANDE_ACCEPTEE
        );

        return reservationRepository.save(reservation);
    }

    // ===== REFUSER =====
    public Reservation refuserReservation(Long id) {
        Reservation reservation = getById(id);
        reservation.setStatut(StatutReservation.REFUSE);
        return reservationRepository.save(reservation);
    }

    // ===== TERMINER =====
    public Reservation terminerReservation(Long id) {
        Reservation reservation = getById(id);
        reservation.setStatut(StatutReservation.TERMINE);

        Ouvrier ouvrier = reservation.getOuvrier();
        ouvrier.setTachesTerminees(ouvrier.getTachesTerminees() + 1);
        ouvrier.setEstDisponible(true);
        ouvrierRepository.save(ouvrier);

        notificationService.envoyerNotification(
                reservation.getClient().getUser(),
                "Service terminé",
                "Évaluez " + ouvrier.getNom(),
                TypeNotification.SERVICE_TERMINE
        );

        return reservationRepository.save(reservation);
    }

    // ===== COMMANDES CLIENT =====
    public List<Reservation> getCommandesClient(Long clientId) {
        return reservationRepository.findByClientId(clientId);
    }

    // ===== COMMANDES OUVRIER =====
    public List<Reservation> getCommandesOuvrier(Long ouvrierId) {
        return reservationRepository.findByOuvrierId(ouvrierId);
    }

    // ===== COMMANDES PAR STATUT =====
    public List<Reservation> getCommandesOuvrierParStatut(Long ouvrierId,
                                                          StatutReservation statut) {
        return reservationRepository.findByOuvrierIdAndStatut(ouvrierId, statut);
    }

    // ===== GAINS OUVRIER =====
    public Double getGainsOuvrier(Long ouvrierId) {
        return reservationRepository
                .findByOuvrierIdAndStatut(ouvrierId, StatutReservation.TERMINE)
                .stream()
                .mapToDouble(Reservation::getPrixTotal)
                .sum();
    }

    // ===== GET BY ID =====
    public Reservation getById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
    }
}