package com.travvite.backend.controller;

import com.travvite.backend.model.Ouvrier;
import com.travvite.backend.service.OuvrierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ouvriers")
@RequiredArgsConstructor
public class OuvrierController {

    private final OuvrierService ouvrierService;

    // ===== LISTE TOUS =====
    @GetMapping
    public ResponseEntity<List<Ouvrier>> getTous() {
        return ResponseEntity.ok(ouvrierService.getTousOuvriers());
    }

    // ===== PAR ID =====
    @GetMapping("/{id}")
    public ResponseEntity<Ouvrier> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ouvrierService.getOuvrierById(id));
    }

    // ===== LES PLUS NOTES =====
    @GetMapping("/plus-notes")
    public ResponseEntity<List<Ouvrier>> getPlusNotes() {
        return ResponseEntity.ok(ouvrierService.getPlusNotes());
    }

    // ===== FILTRAGE + RECHERCHE =====
    @GetMapping("/filtrer")
    public ResponseEntity<List<Ouvrier>> filtrer(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String categorie,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String langue,
            @RequestParam(required = false) String disponibilite,
            @RequestParam(required = false) Boolean accepteEnfants,
            @RequestParam(required = false) String niveauEtude,
            @RequestParam(required = false) String nationalite,
            @RequestParam(required = false) Double prixMin,
            @RequestParam(required = false) Double prixMax) {
        return ResponseEntity.ok(ouvrierService.filtrer(
                nom, categorie, genre, langue, disponibilite,
                accepteEnfants, niveauEtude, nationalite, prixMin, prixMax
        ));
    }

    // ===== CHANGER DISPONIBILITE =====
    @PutMapping("/{id}/disponibilite")
    public ResponseEntity<Ouvrier> changerDisponibilite(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
        return ResponseEntity.ok(
                ouvrierService.changerDisponibilite(id, body.get("estDisponible"))
        );
    }
}
