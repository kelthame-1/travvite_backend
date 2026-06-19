package com.travvite.backend.service;

import com.travvite.backend.model.*;
import com.travvite.backend.repository.*;
import com.travvite.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final OuvrierRepository ouvrierRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ===== INSCRIPTION CLIENT =====
    public Map<String, Object> inscrireClient(String telephone, String password,
                                              String nom, String prenom, String ville) {
        if (userRepository.existsByTelephone(telephone)) {
            throw new RuntimeException("Téléphone déjà utilisé");
        }

        User user = User.builder()
                .telephone(telephone)
                .password(passwordEncoder.encode(password))
                .role(Role.CLIENT)
                .build();
        userRepository.save(user);

        Client client = Client.builder()
                .user(user)
                .nom(nom)
                .prenom(prenom)
                .ville(ville)
                .build();
        clientRepository.save(client);

        String token = jwtUtil.generateToken(telephone, Role.CLIENT.name());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", "CLIENT");
        response.put("id", client.getId());
        response.put("nom", client.getNom());
        response.put("prenom", client.getPrenom());
        response.put("telephone", user.getTelephone());
        response.put("ville", client.getVille());
        return response;
    }

    // ===== INSCRIPTION OUVRIER ETAPE 1 =====
    public Map<String, Object> inscrireOuvrierEtape1(String telephone, String password,
                                                     String nom, String prenom, String ville) {
        if (userRepository.existsByTelephone(telephone)) {
            throw new RuntimeException("Téléphone déjà utilisé");
        }

        User user = User.builder()
                .telephone(telephone)
                .password(passwordEncoder.encode(password))
                .role(Role.OUVRIER)
                .build();
        userRepository.save(user);

        Ouvrier ouvrier = Ouvrier.builder()
                .user(user)
                .nom(nom)
                .prenom(prenom)
                .ville(ville)
                .build();
        ouvrierRepository.save(ouvrier);

        String token = jwtUtil.generateToken(telephone, Role.OUVRIER.name());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", "OUVRIER");
        response.put("id", ouvrier.getId());
        response.put("nom", ouvrier.getNom());
        response.put("prenom", ouvrier.getPrenom());
        response.put("telephone", user.getTelephone());
        return response;
    }

    // ===== INSCRIPTION OUVRIER ETAPE 2 =====
    public Map<String, Object> inscrireOuvrierEtape2(Long ouvrierId, String categorie,
                                                     String genre, String langue,
                                                     String disponibilite, Boolean accepteEnfants,
                                                     String niveauEtude, String nationalite,
                                                     Double prixJournalier, Double prixMensuel) {
        Ouvrier ouvrier = ouvrierRepository.findById(ouvrierId)
                .orElseThrow(() -> new RuntimeException("Ouvrier non trouvé"));

        ouvrier.setCategorie(categorie);
        ouvrier.setGenre(genre);
        ouvrier.setLangue(langue);
        ouvrier.setDisponibilite(disponibilite);
        ouvrier.setAccepteEnfants(accepteEnfants);
        ouvrier.setNiveauEtude(niveauEtude);
        ouvrier.setNationalite(nationalite);
        ouvrier.setPrixJournalier(prixJournalier);
        ouvrier.setPrixMensuel(prixMensuel);
        ouvrierRepository.save(ouvrier);

        Map<String, Object> response = new HashMap<>();
        response.put("role", "OUVRIER");
        response.put("id", ouvrier.getId());
        response.put("nom", ouvrier.getNom());
        response.put("prenom", ouvrier.getPrenom());
        response.put("telephone", ouvrier.getUser().getTelephone());
        response.put("ville", ouvrier.getVille());
        response.put("categorie", ouvrier.getCategorie());
        response.put("genre", ouvrier.getGenre());
        response.put("langue", ouvrier.getLangue());
        response.put("disponibilite", ouvrier.getDisponibilite());
        response.put("accepteEnfants", ouvrier.getAccepteEnfants());
        response.put("niveauEtude", ouvrier.getNiveauEtude());
        response.put("nationalite", ouvrier.getNationalite());
        response.put("prixJournalier", ouvrier.getPrixJournalier());
        response.put("prixMensuel", ouvrier.getPrixMensuel());
        return response;
    }

    // ===== LOGIN =====
    public Map<String, Object> login(String telephone, String password) {
        User user = userRepository.findByTelephone(telephone)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        String token = jwtUtil.generateToken(telephone, user.getRole().name());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole().name());
        response.put("telephone", user.getTelephone());

        if (user.getRole() == Role.CLIENT) {
            Client client = clientRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));
            response.put("id", client.getId());
            response.put("nom", client.getNom());
            response.put("prenom", client.getPrenom());
            response.put("ville", client.getVille());
        } else {
            Ouvrier ouvrier = ouvrierRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Ouvrier non trouvé"));
            response.put("id", ouvrier.getId());
            response.put("nom", ouvrier.getNom());
            response.put("prenom", ouvrier.getPrenom());
            response.put("ville", ouvrier.getVille());
            response.put("categorie", ouvrier.getCategorie());
            response.put("genre", ouvrier.getGenre());
            response.put("langue", ouvrier.getLangue());
            response.put("prixJournalier", ouvrier.getPrixJournalier());
            response.put("estDisponible", ouvrier.getEstDisponible());
        }

        return response;
    }

    // ===== UPDATE FCM TOKEN =====
    public void updateFcmToken(String telephone, String fcmToken) {
        User user = userRepository.findByTelephone(telephone)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }
}