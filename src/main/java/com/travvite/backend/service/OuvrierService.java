package com.travvite.backend.service;

import com.travvite.backend.model.Ouvrier;
import com.travvite.backend.repository.OuvrierRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OuvrierService {

    private final OuvrierRepository ouvrierRepository;

    // ===== LISTE TOUS =====
    public List<Ouvrier> getTousOuvriers() {
        return ouvrierRepository.findAll();
    }

    // ===== PAR ID =====
    public Ouvrier getOuvrierById(Long id) {
        return ouvrierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ouvrier non trouvé"));
    }

    // ===== PAR USER ID =====
    public Ouvrier getOuvrierByUserId(Long userId) {
        return ouvrierRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Ouvrier non trouvé"));
    }

    // ===== LES PLUS NOTES (directement en DB) =====
    public List<Ouvrier> getPlusNotes() {
        return ouvrierRepository.findAll(
                        Sort.by(Sort.Direction.DESC, "noteMoyenne"))
                .stream().limit(10).toList();
    }

    // ===== FILTRAGE + RECHERCHE (en DB via Specification) =====
    public List<Ouvrier> filtrer(String nom, String categorie, String genre,
                                 String langue, String disponibilite,
                                 Boolean accepteEnfants, String niveauEtude,
                                 String nationalite, Double prixMin, Double prixMax) {

        Specification<Ouvrier> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nom != null && !nom.isEmpty()) {
                Predicate nomP = cb.like(cb.lower(root.get("nom")),
                        "%" + nom.toLowerCase() + "%");
                Predicate prenomP = cb.like(cb.lower(root.get("prenom")),
                        "%" + nom.toLowerCase() + "%");
                predicates.add(cb.or(nomP, prenomP));
            }
            if (categorie != null)
                predicates.add(cb.equal(
                        cb.lower(root.get("categorie")),
                        categorie.toLowerCase()));
            if (genre != null)
                predicates.add(cb.equal(
                        cb.lower(root.get("genre")),
                        genre.toLowerCase()));
            if (langue != null)
                predicates.add(cb.equal(
                        cb.lower(root.get("langue")),
                        langue.toLowerCase()));
            if (disponibilite != null)
                predicates.add(cb.equal(
                        cb.lower(root.get("disponibilite")),
                        disponibilite.toLowerCase()));
            if (accepteEnfants != null)
                predicates.add(cb.equal(
                        root.get("accepteEnfants"), accepteEnfants));
            if (niveauEtude != null)
                predicates.add(cb.equal(
                        cb.lower(root.get("niveauEtude")),
                        niveauEtude.toLowerCase()));
            if (nationalite != null)
                predicates.add(cb.equal(
                        cb.lower(root.get("nationalite")),
                        nationalite.toLowerCase()));
            if (prixMin != null)
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("prixJournalier"), prixMin));
            if (prixMax != null)
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("prixJournalier"), prixMax));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return ouvrierRepository.findAll(spec);
    }

    // ===== CHANGER DISPONIBILITE =====
    public Ouvrier changerDisponibilite(Long id, Boolean estDisponible) {
        Ouvrier ouvrier = getOuvrierById(id);
        ouvrier.setEstDisponible(estDisponible);
        return ouvrierRepository.save(ouvrier);
    }

    // ===== METTRE A JOUR NOTE =====
    public void mettreAJourNote(Long ouvrierId, Double nouvelleMoyenne) {
        Ouvrier ouvrier = getOuvrierById(ouvrierId);
        ouvrier.setNoteMoyenne(nouvelleMoyenne);
        ouvrier.setNombreAvis(ouvrier.getNombreAvis() + 1);
        ouvrierRepository.save(ouvrier);
    }
}