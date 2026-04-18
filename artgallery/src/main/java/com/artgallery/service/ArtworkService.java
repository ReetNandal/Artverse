package com.artgallery.service;

import com.artgallery.model.Artwork;
import com.artgallery.repository.ArtworkRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtworkService {

    private final ArtworkRepository artworkRepository;

    public ArtworkService(ArtworkRepository artworkRepository) {
        this.artworkRepository = artworkRepository;
    }

    public List<Artwork> getAllArtworks() {
        return artworkRepository.findAll();
    }

    public List<Artwork> getArtworksByCategory(String category) {
        return artworkRepository.findByCategoryIgnoreCase(category);
    }

    public List<String> getAllCategories() {
        return artworkRepository.findDistinctCategories();
    }

    public List<Artwork> searchArtworks(String q, String category) {
        return artworkRepository.search(q, category);
    }

    public Artwork saveArtwork(Artwork artwork) {
        return artworkRepository.save(artwork);
    }

    public Artwork getArtworkById(Long id) {
        return artworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found with id: " + id));
    }

}
