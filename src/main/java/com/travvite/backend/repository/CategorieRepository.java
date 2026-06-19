package com.travvite.backend.repository;

import com.travvite.backend.model.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    Optional<Categorie> findBySlug(String slug);
}
