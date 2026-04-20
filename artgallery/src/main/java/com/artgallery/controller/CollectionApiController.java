package com.artgallery.controller;

import com.artgallery.model.CollectionItem;
import com.artgallery.model.User;
import com.artgallery.repository.UserRepository;
import com.artgallery.service.CollectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CollectionApiController {

    private final CollectionService collectionService;
    private final UserRepository userRepository;

    public CollectionApiController(CollectionService collectionService, UserRepository userRepository) {
        this.collectionService = collectionService;
        this.userRepository = userRepository;
    }

    @GetMapping("/my-collection")
    public ResponseEntity<?> myCollection(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "latest") String sort,
            Principal principal
    ) {
        if (principal == null) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("message", "Please login");
            return ResponseEntity.status(401).body(errorMap);
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));

        List<CollectionItem> items = collectionService.getMyCollection(user.getId(), category, q, sort);

        List<Map<String, Object>> dto = items.stream()
                .map(i -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("collectionId", i.getId());
                    m.put("artworkId", i.getArtworkId());
                    m.put("title", i.getArtwork() != null ? i.getArtwork().getTitle() : "Untitled");
                    m.put("artistName", i.getArtwork() != null ? i.getArtwork().getArtistName() : "Unknown");
                    m.put("category", i.getArtwork() != null ? i.getArtwork().getCategory() : "");
                    m.put("imageUrl", i.getArtwork() != null ? i.getArtwork().getImageUrl() : "");
                    m.put("pricePaid", i.getPricePaid());
                    m.put("acquisitionType", i.getAcquisitionType() != null ? i.getAcquisitionType() : "OWNED");
                    return m;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("count", dto.size());
        response.put("items", dto);

        return ResponseEntity.ok(response);
    }
}