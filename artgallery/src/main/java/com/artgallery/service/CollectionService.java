package com.artgallery.service;

import com.artgallery.model.Artwork;
import com.artgallery.model.CollectionItem;
import com.artgallery.repository.ArtworkRepository;
import com.artgallery.repository.CollectionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final ArtworkRepository artworkRepository;

    public CollectionService(CollectionRepository collectionRepository, ArtworkRepository artworkRepository) {
        this.collectionRepository = collectionRepository;
        this.artworkRepository = artworkRepository;
    }

    public List<CollectionItem> getMyCollection(Long userId, String category, String q, String sort) {
        List<CollectionItem> items = collectionRepository.findByUserIdOrderByIdDesc(userId);

        // attach artwork info for UI cards
        List<Long> artworkIds = items.stream()
                .map(CollectionItem::getArtworkId)
                .filter(id -> id != null)
                .distinct()
                .toList();

        Map<Long, Artwork> artMap = artworkRepository.findAllById(artworkIds).stream()
                .collect(Collectors.toMap(Artwork::getId, a -> a));

        items.forEach(i -> i.setArtwork(artMap.get(i.getArtworkId())));

        // filter: category
        if (category != null && !category.isBlank()) {
            items = items.stream()
                    .filter(i -> i.getArtwork() != null)
                    .filter(i -> i.getArtwork().getCategory() != null)
                    .filter(i -> i.getArtwork().getCategory().equalsIgnoreCase(category))
                    .toList();
        }

        // filter: search q (title/artist)
        if (q != null && !q.isBlank()) {
            String qq = q.toLowerCase();
            items = items.stream()
                    .filter(i -> i.getArtwork() != null)
                    .filter(i -> {
                        String t = i.getArtwork().getTitle() == null ? "" : i.getArtwork().getTitle().toLowerCase();
                        String a = i.getArtwork().getArtistName() == null ? "" : i.getArtwork().getArtistName().toLowerCase();
                        return t.contains(qq) || a.contains(qq);
                    })
                    .toList();
        }

        // sort
        if (sort == null || sort.isBlank() || "latest".equalsIgnoreCase(sort)) {
            items = items.stream()
                    .sorted(Comparator.comparing(CollectionItem::getId).reversed())
                    .toList();
        } else if ("title".equalsIgnoreCase(sort)) {
            items = items.stream()
                    .sorted(Comparator.comparing(i -> safe(i.getArtwork() != null ? i.getArtwork().getTitle() : "")))
                    .toList();
        } else if ("priceHigh".equalsIgnoreCase(sort)) {
            items = items.stream()
                    .sorted((x, y) -> Double.compare(safeNum(y.getPricePaid()), safeNum(x.getPricePaid())))
                    .toList();
        } else if ("priceLow".equalsIgnoreCase(sort)) {
            items = items.stream()
                    .sorted(Comparator.comparingDouble(i -> safeNum(i.getPricePaid())))
                    .toList();
        }

        return items;
    }

    // ✅ THIS METHOD inserts into DB collections table when an auction ends
    public void addAuctionWinToCollections(Long auctionId, Long winnerId, Long artworkId, Double finalPrice) {
        if (auctionId == null || winnerId == null || artworkId == null) return;

        // avoid duplicate insert
        if (collectionRepository.existsByAuctionId(auctionId)) return;

        LocalDateTime now = LocalDateTime.now();

        CollectionItem c = new CollectionItem();
        c.setAuctionId(auctionId);
        c.setUserId(winnerId);
        c.setArtworkId(artworkId);

        // ✅ update BOTH columns
        c.setPurchasePrice(finalPrice);
        c.setPricePaid(finalPrice);

        c.setAcquisitionType("AUCTION_WIN");
        c.setWonDate(now);
        c.setAcquiredAt(now);

        // optional defaults
        c.setShippingStatus("PENDING");
        c.setCertificateUrl(null);

        collectionRepository.save(c);
    }

    private String safe(String s) { return s == null ? "" : s; }
    private double safeNum(Double d) { return d == null ? 0.0 : d; }
}