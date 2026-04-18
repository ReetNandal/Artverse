package com.artgallery.service;

import com.artgallery.dto.HeatmapItem;
import com.artgallery.model.Auction;
import com.artgallery.model.Artwork;
import com.artgallery.model.Bid;
import com.artgallery.repository.AuctionRepository;
import com.artgallery.repository.ArtworkRepository;
import com.artgallery.repository.BidRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuctionService {

    private final AuctionRepository repo;
    private final ArtworkRepository artworkRepo;
    private final BidRepository bidRepository;
    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Kolkata");

    // ✅ NEW: to insert auction wins into collections table
    private final CollectionService collectionService;

    public AuctionService(AuctionRepository repo,
                          ArtworkRepository artworkRepo,
                          BidRepository bidRepository,
                          CollectionService collectionService) {
        this.repo = repo;
        this.artworkRepo = artworkRepo;
        this.bidRepository = bidRepository;
        this.collectionService = collectionService;
    }

    private String resolveStatus(Auction auction) {
        LocalDateTime now = LocalDateTime.now(APP_ZONE);

        if (auction.getStartTime() != null && now.isBefore(auction.getStartTime())) {
            return "UPCOMING";
        }

        if (auction.getEndTime() != null && now.isAfter(auction.getEndTime())) {
            return "ENDED";
        }

        return "LIVE";
    }

    // ✅ PERMANENT FIX:
    // Do not auto-save status back to DB.
    // Status should be calculated dynamically from startTime and endTime.
    /*
    @Scheduled(fixedRate = 10000)
    public void updateAuctionStatuses() {
        List<Auction> all = repo.findAll();

        for (Auction auction : all) {
            String newStatus = resolveStatus(auction);

            if (!newStatus.equalsIgnoreCase(auction.getStatus())) {
                auction.setStatus(newStatus);

                if ("ENDED".equalsIgnoreCase(newStatus)) {
                    finalizeAuctionAndAddToCollection(auction);
                }

                repo.save(auction);
            }
        }
    }
    */
    private void finalizeAuctionAndAddToCollection(Auction auction) {
        if (auction.getWinnerId() != null) {
            if (auction.getPaymentStatus() == null || auction.getPaymentStatus().isBlank()) {
                auction.setPaymentStatus("PENDING");
            }
            if (auction.getPaymentDeadline() == null && auction.getEndTime() != null) {
                auction.setPaymentDeadline(auction.getEndTime().plusHours(24));
            }
            repo.save(auction);
            return;
        }

        Optional<Bid> topBidOpt =
                bidRepository.findTop1ByAuction_IdOrderByAmountDescBidTimeDesc(auction.getId());

        if (topBidOpt.isEmpty()) return;

        Bid topBid = topBidOpt.get();
        if (topBid.getUser() == null) return;

        Long winnerId = topBid.getUser().getId();
        auction.setWinnerId(winnerId);
        auction.setPaymentStatus("PENDING");

        if (auction.getEndTime() != null) {
            auction.setPaymentDeadline(auction.getEndTime().plusHours(24));
        }

        repo.save(auction);
    }

    public List<String> getAllCategories() {
        return artworkRepo.findDistinctCategories();
    }

    public List<Auction> getAuctionsByFilters(String status, String category) {
        List<Auction> auctions;

        boolean hasStatus = status != null && !status.isBlank();
        boolean hasCategory = category != null && !category.isBlank();

        // ✅ IMPORTANT FIX:
        // Always fetch broad data first, then calculate real-time status in Java.
        if (hasCategory) {
            auctions = repo.findByCategory(category);
        } else {
            auctions = repo.findAll();
        }

        attachArtworkInfo(auctions);

        // ✅ status should always match current time, not stale DB value
        for (Auction auction : auctions) {
            auction.setStatus(resolveStatus(auction));
        }

        // ✅ filter by real-time status after dynamic calculation
        if (hasStatus) {
            String finalStatus = status.toUpperCase();

            auctions = auctions.stream()
                    .filter(a -> finalStatus.equalsIgnoreCase(a.getStatus()))
                    .collect(Collectors.toList());
        }

        return auctions;
    }

    public List<Auction> getLiveAuctions() {
        return getAuctionsByFilters("LIVE", null);
    }

    public List<Auction> getUpcomingAuctions() {
        return getAuctionsByFilters("UPCOMING", null);
    }

    public List<Auction> getClosedAuctions() {
        return getAuctionsByFilters("ENDED", null);
    }

    public Auction getAuctionById(Long id) {
        Auction auction = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        auction.setStatus(resolveStatus(auction));

        if (auction.getArtworkId() != null) {
            Artwork art = artworkRepo.findById(auction.getArtworkId()).orElse(null);

            if (art != null) {
                auction.setArtworkImageUrl(art.getImageUrl());
                auction.setArtworkTitle(art.getTitle());
                auction.setArtworkCategory(art.getCategory());
                auction.setArtistName(art.getArtistName());
                auction.setArtworkDescription(art.getDescription());

                // NEW FIELDS
                auction.setTechnique(art.getTechnique());
                auction.setYearMade(art.getYearMade());
                auction.setSizeDimension(art.getSizeDimension());
                auction.setOrientation(art.getOrientation());

                if (art.getBasePrice() != null) {
                    auction.setBasePrice(art.getBasePrice().doubleValue());
                }

                if (art.getEstimatedPrice() != null) {
                    auction.setEstimatedPrice(art.getEstimatedPrice().doubleValue());
                }
            }
        }

        if (auction.getEndTime() != null) {
            auction.setEndTimeMillis(
                    auction.getEndTime()
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()
            );
        }

        auction.setTotalBids(bidRepository.countByAuction_Id(auction.getId()));
        return auction;
    }

    private void attachArtworkInfo(List<Auction> auctions) {
        List<Long> artworkIds = auctions.stream()
                .map(Auction::getArtworkId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (artworkIds.isEmpty()) {
            return;
        }

        Map<Long, Artwork> map = artworkRepo.findAllById(artworkIds)
                .stream()
                .collect(Collectors.toMap(Artwork::getId, a -> a));

        for (Auction auction : auctions) {
            Artwork art = map.get(auction.getArtworkId());

            if (art != null) {
                auction.setArtworkImageUrl(art.getImageUrl());
                auction.setArtworkTitle(art.getTitle());
                auction.setArtworkCategory(art.getCategory());
                auction.setArtistName(art.getArtistName());
                auction.setArtworkDescription(art.getDescription());

                auction.setTechnique(art.getTechnique());
                auction.setYearMade(art.getYearMade());
                auction.setSizeDimension(art.getSizeDimension());
                auction.setOrientation(art.getOrientation());

                if (art.getBasePrice() != null) {
                    auction.setBasePrice(art.getBasePrice().doubleValue());
                }

                if (art.getEstimatedPrice() != null) {
                    auction.setEstimatedPrice(art.getEstimatedPrice().doubleValue());
                }
            }

            if (auction.getEndTime() != null) {
                auction.setEndTimeMillis(
                        auction.getEndTime()
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                                .toEpochMilli()
                );
            }

            auction.setTotalBids(bidRepository.countByAuction_Id(auction.getId()));
        }
    }

    public List<HeatmapItem> getDynamicHeatmap() {
        List<Object[]> rows = repo.getLiveAuctionVolumeByCategory();

        double totalVolume = 0.0;
        for (Object[] row : rows) {
            if (row[1] != null) {
                totalVolume += ((Number) row[1]).doubleValue();
            }
        }

        List<HeatmapItem> result = new ArrayList<>();

        for (int i = 0; i < rows.size() && i < 4; i++) {
            Object[] row = rows.get(i);

            String category = row[0] != null ? row[0].toString() : "Unknown";
            double volume = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
            double percentage = calculatePercentage(volume, totalVolume);

            result.add(new HeatmapItem(category, percentage, getHeatmapCssClass(i)));
        }

        return result;
    }

    private double calculatePercentage(double value, double total) {
        if (total == 0) {
            return 0.0;
        }
        return Math.round((value / total) * 1000.0) / 10.0;
    }

    private String getHeatmapCssClass(int index) {
        switch (index) {
            case 0:
                return "heatmap-paintings";
            case 1:
                return "heatmap-digital";
            case 2:
                return "heatmap-sculpture";
            case 3:
                return "heatmap-photography";
            default:
                return "heatmap-paintings";
        }
    }
    public List<Auction> getResultAuctionsForUser(Long userId) {
        List<Auction> auctions = repo.findAllWonAuctions(userId);
        attachArtworkInfo(auctions);

        for (Auction auction : auctions) {
            enrichResultAuction(auction);
        }

        return auctions;
    }

    public Auction getCheckoutAuction(Long auctionId, Long userId) {
        Auction auction = repo.findWonAuctionByIdAndWinnerId(auctionId, userId)
                .orElseThrow(() -> new RuntimeException("Result auction not found"));

        auction = getAuctionById(auction.getId());

        auction.setPaymentStatus(
                auction.getPaymentStatus() == null || auction.getPaymentStatus().isBlank()
                        ? "PENDING"
                        : auction.getPaymentStatus()
        );

        if (auction.getPaymentDeadline() == null && auction.getEndTime() != null) {
            auction.setPaymentDeadline(auction.getEndTime().plusHours(24));
        }

        enrichResultAuction(auction);

        double shipping = 75.0;
        double tax = auction.getCurrentBid() != null ? auction.getCurrentBid() * 0.08 : 0.0;
        double total = (auction.getCurrentBid() != null ? auction.getCurrentBid() : 0.0) + shipping + tax;

        auction.setShippingCharge(shipping);
        auction.setTaxAmount(tax);
        auction.setTotalPayable(total);

        return auction;
    }

    public String resolveResultStatus(Auction auction) {
        LocalDateTime now = LocalDateTime.now(APP_ZONE);

        String paymentStatus = auction.getPaymentStatus();
        if ("PAID".equalsIgnoreCase(paymentStatus)) {
            return "PAID";
        }

        if (auction.getPaymentDeadline() != null && now.isAfter(auction.getPaymentDeadline())) {
            return "EXPIRED";
        }

        return "PAYMENT_PENDING";
    }

    public void enrichResultAuction(Auction auction) {
        if (auction.getPaymentStatus() == null || auction.getPaymentStatus().isBlank()) {
            auction.setPaymentStatus("PENDING");
        }

        if (auction.getPaymentDeadline() == null && auction.getEndTime() != null) {
            auction.setPaymentDeadline(auction.getEndTime().plusHours(24));
        }

        String resultStatus = resolveResultStatus(auction);
        auction.setResultStatus(resultStatus);

        switch (resultStatus) {
            case "PAID" -> auction.setResultButtonText("View Details");
            case "EXPIRED" -> auction.setResultButtonText("Auction Expired");
            default -> auction.setResultButtonText("Proceed to Checkout");
        }
    }

    public void markAuctionPaid(Auction auction) {
        auction.setPaymentStatus("PAID");
        auction.setPaymentPaidAt(LocalDateTime.now(APP_ZONE));
        repo.save(auction);

        collectionService.addAuctionWinToCollections(
                auction.getId(),
                auction.getWinnerId(),
                auction.getArtworkId(),
                auction.getCurrentBid()
        );
    }

}