package com.artgallery.service;

import com.artgallery.dto.ListingForm;
import com.artgallery.model.Artwork;
import com.artgallery.model.Auction;
import com.artgallery.model.User;
import com.artgallery.repository.ArtworkRepository;
import com.artgallery.repository.AuctionRepository;
import com.artgallery.repository.BidRepository;
import com.artgallery.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ListingService {

    private final ArtworkRepository artworkRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;

    public ListingService(ArtworkRepository artworkRepository,
                          AuctionRepository auctionRepository,
                          UserRepository userRepository,
                          BidRepository bidRepository) {
        this.artworkRepository = artworkRepository;
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
    }

    @Transactional
    public void createListing(String username, ListingForm form) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Artwork artwork = new Artwork();
        artwork.setTitle(form.getTitle());
        artwork.setImageUrl(form.getImageUrl());
        artwork.setArtistName(form.getArtistName());
        artwork.setDescription(form.getDescription());
        artwork.setCategory(form.getCategory());
        artwork.setTechnique(form.getTechnique());
        artwork.setYearMade(form.getYearMade());
        artwork.setSizeDimension(form.getSizeDimension());
        artwork.setOrientation(form.getOrientation());

        if (form.getStartingBid() != null) {
            artwork.setBasePrice(BigDecimal.valueOf(form.getStartingBid()));
        }

        if (form.getReservePrice() != null) {
            artwork.setEstimatedPrice(BigDecimal.valueOf(form.getReservePrice()));
        }

        artwork.setCreatedAt(LocalDateTime.now());

        Artwork savedArtwork = artworkRepository.save(artwork);

        LocalDateTime startTime = form.getStartDate().atStartOfDay();
        LocalDateTime endTime = startTime.plusDays(form.getDurationDays());

        Auction auction = new Auction();
        auction.setArtworkId(savedArtwork.getId());
        auction.setSellerId(user.getId());
        auction.setTitle(form.getTitle() + " Auction");
        auction.setDescription(form.getDescription());
        auction.setStartTime(startTime);
        auction.setEndTime(endTime);
        auction.setCurrentBid(form.getStartingBid());
        auction.setBidIncrement(form.getBidIncrement());
        auction.setReserveMet(false);
        auction.setWinnerId(null);
        auction.setCreatedAt(LocalDateTime.now());
        auction.setStatus(resolveStatus(startTime, endTime));

        auctionRepository.save(auction);
    }

    public List<Auction> getMyListings(Long userId) {
        List<Auction> listings = auctionRepository.findBySellerIdOrderByIdDesc(userId);

        for (Auction auction : listings) {
            auction.setStatus(resolveStatus(auction.getStartTime(), auction.getEndTime()));

            if (auction.getArtworkId() != null) {
                Artwork art = artworkRepository.findById(auction.getArtworkId()).orElse(null);

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
            }

            auction.setTotalBids(bidRepository.countByAuction_Id(auction.getId()));
        }

        return listings;
    }

    private String resolveStatus(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(startTime)) {
            return "UPCOMING";
        }
        if (now.isAfter(endTime)) {
            return "ENDED";
        }
        return "LIVE";
    }
}