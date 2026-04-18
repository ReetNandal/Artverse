package com.artgallery.repository;

import com.artgallery.model.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

    List<Artwork> findByCategoryIgnoreCase(String category);

    @Query("SELECT DISTINCT a.category FROM Artwork a WHERE a.category IS NOT NULL")
    List<String> findDistinctCategories();

    // ✅ Case-insensitive search across title, artist, category
    @Query("""
        SELECT a FROM Artwork a
        WHERE (:category IS NULL OR :category = '' OR LOWER(a.category) = LOWER(:category))
          AND (
               LOWER(a.title) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(a.artistName) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(a.category) LIKE LOWER(CONCAT('%', :q, '%'))
          )
    """)
    List<Artwork> search(@Param("q") String q, @Param("category") String category);
}