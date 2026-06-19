package com.travvite.backend.controller;

import com.travvite.backend.model.Evaluation;
import com.travvite.backend.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    // ===== CREER EVALUATION =====
    @PostMapping
    public ResponseEntity<Evaluation> creer(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(evaluationService.creerEvaluation(
                Long.valueOf(body.get("reservationId").toString()),
                (Integer) body.get("note"),
                (String) body.get("tags"),
                (String) body.get("commentaire")
        ));
    }

    // ===== EVALUATIONS PAR OUVRIER =====
    @GetMapping("/ouvrier/{ouvrierId}")
    public ResponseEntity<List<Evaluation>> getEvaluationsOuvrier(
            @PathVariable Long ouvrierId) {
        return ResponseEntity.ok(
                evaluationService.getEvaluationsOuvrier(ouvrierId));
    }
}