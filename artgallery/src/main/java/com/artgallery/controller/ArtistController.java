package com.artgallery.controller;

import com.artgallery.model.Artist;
import com.artgallery.service.ArtistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping("/artists")
    public String showArtistsPage(@RequestParam(value = "q", required = false) String q, Model model) {
        List<Artist> artists = artistService.searchArtists(q);

        model.addAttribute("artists", artists);
        model.addAttribute("searchQuery", q);

        return "artists";
    }
}