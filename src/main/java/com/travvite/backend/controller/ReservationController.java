package com.travvite.backend.controller;

import com.travvite.backend.model.Reservation;
import com.travvite.backend.model.StatutReservation;
import com.travvite.backend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // ===== CREER RESERVATION =====
    @PostMapping
    public ResponseEntity<Reservation> creer(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(reservationService.creerReservation(
                Long.valueOf(body.get("clientId").toString()),
                Long.valueOf(body.get("ouvrierId").toString()),
                (String) body.get("description"),
                LocalDate.parse((String) body.get("dateService")),
                LocalTime.parse((String) body.get("heureService")),
                (Integer) body.get("nbHeures"),
                (String) body.get("adresse")
        ));
    }

    // ===== ACCEPTER =====
    @PatchMapping("/{id}/accepter")
    public ResponseEntity<Reservation> accepter(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.accepterReservation(id));
    }

    // ===== REFUSER =====
    @PatchMapping("/{id}/refuser")
    public ResponseEntity<Reservation> refuser(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.refuserReservation(id));
    }

    // ===== TERMINER =====
    @PatchMapping("/{id}/terminer")
    public ResponseEntity<Reservation> terminer(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.terminerReservation(id));
    }

    // ===== GET COMMANDES CLIENT =====
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Reservation>> getCommandesClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(reservationService.getCommandesClient(clientId));
    }

    // ===== GET COMMANDES OUVRIER =====
    @GetMapping("/ouvrier/{ouvrierId}")
    public ResponseEntity<List<Reservation>> getCommandesOuvrier(@PathVariable Long ouvrierId) {
        return ResponseEntity.ok(reservationService.getCommandesOuvrier(ouvrierId));
    }

    // ===== GET BY ID =====
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getById(id));
    }
}