package com.artgallery.repository;

import com.artgallery.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistRepository extends JpaRepository<Artist, Integer> {

    List<Artist> findByNameContainingIgnoreCaseOrStyleCategoryContainingIgnoreCase(String name, String styleCategory);
}