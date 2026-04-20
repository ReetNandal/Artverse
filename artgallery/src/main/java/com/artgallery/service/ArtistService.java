package com.artgallery.service;

import com.artgallery.model.Artist;
import com.artgallery.repository.ArtistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    public List<Artist> searchArtists(String query) {
        if (query == null || query.trim().isEmpty()) {
            return artistRepository.findAll();
        }
        return artistRepository.findByNameContainingIgnoreCaseOrStyleCategoryContainingIgnoreCase(query.trim(), query.trim());
    }
}