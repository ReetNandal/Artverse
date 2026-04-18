package com.artgallery.controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import com.artgallery.model.Artwork;
import com.artgallery.service.ArtworkService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/artworks")
public class ArtworkController {


    private final ArtworkService artworkService;

    public ArtworkController(ArtworkService artworkService) {
        this.artworkService = artworkService;
    }

    @GetMapping
    public String listArtworks(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,   // 2 rows * 4 cards
            @RequestParam(required = false) Long seed,
            @RequestParam(required = false, defaultValue = "false") boolean fragment,
            Model model) {

        List<Artwork> all;

        if (q != null && !q.isBlank()) {
            all = artworkService.searchArtworks(q, category);
        } else if (category != null && !category.isBlank()) {
            all = artworkService.getArtworksByCategory(category);
        } else {
            all = artworkService.getAllArtworks();
        }

        // Shuffle on full page reload using a seed
        if (seed == null) seed = System.currentTimeMillis();
        Collections.shuffle(all, new Random(seed));

        int from = page * size;
        int to = Math.min(from + size, all.size());
        List<Artwork> pageList = (from < all.size()) ? all.subList(from, to) : List.of();

        model.addAttribute("artworks", pageList);
        model.addAttribute("totalCount", all.size());
        model.addAttribute("categories", artworkService.getAllCategories());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("q", q);
        model.addAttribute("seed", seed);
        model.addAttribute("nextPage", page + 1);
        model.addAttribute("hasMore", to < all.size());

        if (fragment) {
            return "artworks :: cards";
        }

        return "artworks";
    }
    @GetMapping("/api/{id}")
    @ResponseBody
    public Artwork getArtworkById(@PathVariable Long id) {
        return artworkService.getArtworkById(id);
    }

}