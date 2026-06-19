package com.travvite.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ouvriers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ouvrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String nom;
    private String prenom;
    private String ville;
    private String categorie;
    private String genre;
    private String langue;
    private String disponibilite;
    private Boolean accepteEnfants;
    private String niveauEtude;
    private String nationalite;
    private Double prixJournalier;
    private Double prixMensuel;
    private String photoUrl;

    @Builder.Default
    private Double noteMoyenne = 0.0;

    @Builder.Default
    private Integer nombreAvis = 0;

    @Builder.Default
    private Integer tachesTerminees = 0;

    @Builder.Default
    private Boolean estDisponible = true;
}