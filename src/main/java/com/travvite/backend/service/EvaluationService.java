package com.travvite.backend.service;

import com.travvite.backend.model.*;
import com.travvite.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final ReservationRepository reservationRepository;
    private final OuvrierRepository ouvrierRepository;

    // ===== CREER EVALUATION =====
    public Evaluation creerEvaluation(Long reservationId, Integer note,
                                      String tags, String commentaire) {
        // تحقق ما تم التقييم مسبقاً
        if (evaluationRepository.existsByReservationId(reservationId)) {
            throw new RuntimeException("Déjà évalué");
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        // تحقق أن الحجز منتهي
        if (reservation.getStatut() != StatutReservation.TERMINE) {
            throw new RuntimeException("Service non terminé");
        }

        Evaluation evaluation = Evaluation.builder()
                .reservation(reservation)
                .client(reservation.getClient())
                .ouvrier(reservation.getOuvrier())
                .note(note)
                .tags(tags)
                .commentaire(commentaire)
                .build();

        evaluationRepository.save(evaluation);

        // تحديث تقييم العامل تلقائياً في DB
        mettreAJourNoteMoyenne(reservation.getOuvrier().getId());

        return evaluation;
    }

    // ===== MISE A JOUR NOTE MOYENNE =====
    private void mettreAJourNoteMoyenne(Long ouvrierId) {
        Double moyenne = evaluationRepository.calculerNoteMoyenne(ouvrierId);
        Long nombreAvis = evaluationRepository.countByOuvrierId(ouvrierId);

        Ouvrier ouvrier = ouvrierRepository.findById(ouvrierId)
                .orElseThrow(() -> new RuntimeException("Ouvrier non trouvé"));

        ouvrier.setNoteMoyenne(moyenne != null ? moyenne : 0.0);
        ouvrier.setNombreAvis(nombreAvis.intValue());
        ouvrierRepository.save(ouvrier);
    }

    // ===== EVALUATIONS PAR OUVRIER =====
    public List<Evaluation> getEvaluationsOuvrier(Long ouvrierId) {
        return evaluationRepository.findByOuvrierId(ouvrierId);
    }
}
