package com.travvite.backend.controller;

import com.travvite.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ===== INSCRIPTION CLIENT =====
    @PostMapping("/inscription/client")
    public ResponseEntity<?> inscrireClient(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(authService.inscrireClient(
                (String) body.get("telephone"),
                (String) body.get("password"),
                (String) body.get("nom"),
                (String) body.get("prenom"),
                (String) body.get("ville")
        ));
    }

    // ===== INSCRIPTION OUVRIER ETAPE 1 =====
    @PostMapping("/inscription/ouvrier/etape1")
    public ResponseEntity<?> inscrireOuvrierEtape1(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(authService.inscrireOuvrierEtape1(
                (String) body.get("telephone"),
                (String) body.get("password"),
                (String) body.get("nom"),
                (String) body.get("prenom"),
                (String) body.get("ville")
        ));
    }

    // ===== INSCRIPTION OUVRIER ETAPE 2 =====
    @PutMapping("/inscription/ouvrier/etape2")
    public ResponseEntity<?> inscrireOuvrierEtape2(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(authService.inscrireOuvrierEtape2(
                Long.valueOf(body.get("id").toString()),
                (String) body.get("categorie"),
                (String) body.get("genre"),
                (String) body.get("langue"),
                (String) body.get("disponibilite"),
                (Boolean) body.get("accepteEnfants"),
                (String) body.get("niveauEtude"),
                (String) body.get("nationalite"),
                Double.valueOf(body.get("prixJournalier").toString()),
                Double.valueOf(body.get("prixMensuel").toString())
        ));
    }

    // ===== LOGIN =====
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authService.login(
                body.get("telephone"),
                body.get("password")
        ));
    }

    // ===== UPDATE FCM TOKEN =====
    @PutMapping("/fcm-token")
    public ResponseEntity<?> updateFcmToken(@RequestBody Map<String, String> body) {
        authService.updateFcmToken(
                body.get("telephone"),
                body.get("fcmToken")
        );
        return ResponseEntity.ok(Map.of("message", "Token mis à jour"));
    }
}