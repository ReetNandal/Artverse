package com.artgallery.service;

import com.artgallery.model.Auction;
import com.artgallery.model.Bid;
import com.artgallery.model.User;
import com.artgallery.repository.AuctionRepository;
import com.artgallery.repository.BidRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final WalletService walletService;
    private final BotService botService;

    public BidService(BidRepository bidRepository,
                      AuctionRepository auctionRepository,
                      WalletService walletService,
                      @Lazy BotService botService) {
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
        this.walletService = walletService;
        this.botService = botService;
    }

    @Transactional
    public void placeBid(Long auctionId, Double amount, User user) {
        placeBidInternal(auctionId, amount, user, false);
    }

    @Transactional
    public void placeBidFromBot(Long auctionId, Double amount, User botUser) {
        placeBidInternal(auctionId, amount, botUser, true);
    }

    private void placeBidInternal(Long auctionId, Double amount, User user, boolean isBot) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        if (!"LIVE".equalsIgnoreCase(auction.getStatus())) {
            throw new RuntimeException("Auction is not live");
        }

        if (user == null) {
            throw new RuntimeException("User is required");
        }

        if (amount == null) {
            throw new RuntimeException("Bid amount is required");
        }

        if (auction.getCurrentBid() == null || auction.getBidIncrement() == null) {
            throw new RuntimeException("Auction bid values are not properly set");
        }

        double minimumAllowed = auction.getCurrentBid() + auction.getBidIncrement();
        if (amount < minimumAllowed) {
            throw new RuntimeException("Bid must be at least ₹" + minimumAllowed);
        }

        Optional<Bid> topBidOpt =
                bidRepository.findTop1ByAuction_IdOrderByAmountDescBidTimeDesc(auctionId);

        if (topBidOpt.isPresent()) {
            Bid topBid = topBidOpt.get();

            if (topBid.getUser() != null
                    && topBid.getUser().getId() != null
                    && topBid.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("You are already the highest bidder");
            }
        }

        if (!isBot) {
            handleWalletForHumanBid(auctionId, amount, user);
        }

        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setUser(user);
        bid.setAmount(amount);
        bid.setBidTime(LocalDateTime.now());
        bid.setIsWithdrawn(false);
        bidRepository.save(bid);

        auction.setCurrentBid(amount);
        auctionRepository.save(auction);

        System.out.println((isBot ? "🤖" : "👤") + " Bid placed by "
                + user.getUsername() + " amount " + amount
                + " on auction " + auctionId);

        // Human bid triggers bot reaction
        if (!isBot) {
            botService.triggerBotReaction(auctionId, user);
        }
    }

    private void handleWalletForHumanBid(Long auctionId, Double amount, User user) {
        BigDecimal newBidAmount = BigDecimal.valueOf(amount);

        Optional<Bid> topBidOpt =
                bidRepository.findTop1ByAuction_IdOrderByAmountDescBidTimeDesc(auctionId);

        if (topBidOpt.isPresent()) {
            Bid topBid = topBidOpt.get();

            if (topBid.getUser() != null) {
                User previousTopBidder = topBid.getUser();

                if (Boolean.TRUE.equals(previousTopBidder.getIsBot())) {
                    // If previous top bidder was a bot, only hold current human amount
                    walletService.holdAmount(user.getId().intValue(), newBidAmount);
                } else {
                    // Release previous human top bidder's held amount
                    walletService.releaseAmount(
                            previousTopBidder.getId().intValue(),
                            BigDecimal.valueOf(topBid.getAmount())
                    );

                    // Hold current human bidder amount
                    walletService.holdAmount(user.getId().intValue(), newBidAmount);
                }
            } else {
                walletService.holdAmount(user.getId().intValue(), newBidAmount);
            }
        } else {
            walletService.holdAmount(user.getId().intValue(), newBidAmount);
        }
    }

    public List<Bid> getRecentBids(Long auctionId) {
        return bidRepository.findTop5ByAuction_IdOrderByBidTimeDesc(auctionId);
    }

    public long getBidCount(Long auctionId) {
        return bidRepository.countByAuction_Id(auctionId);
    }

    public double getCurrentBid(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));
        return auction.getCurrentBid();
    }
}