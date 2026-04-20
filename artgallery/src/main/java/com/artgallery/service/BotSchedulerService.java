package com.artgallery.service;

import com.artgallery.model.Auction;
import com.artgallery.repository.AuctionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class BotSchedulerService {

    private final AuctionRepository auctionRepository;
    private final BotService botService;

    private final Random random = new Random();

    public BotSchedulerService(AuctionRepository auctionRepository,
                               BotService botService) {
        this.auctionRepository = auctionRepository;
        this.botService = botService;
    }

    // Every 2 minutes check live auctions
    @Scheduled(fixedRate = 120000)
    public void triggerBotsOnLiveAuctions() {
        System.out.println("🤖 Bot scheduler running at: " + LocalDateTime.now());

        List<Auction> liveAuctions = auctionRepository.findByStatus("LIVE");
        System.out.println("🤖 Live auctions found: " + liveAuctions.size());

        for (Auction auction : liveAuctions) {
            try {
                if (auction.getId() == null) continue;
                if (auction.getCurrentBid() == null) continue;
                if (auction.getBidIncrement() == null) continue;
                if (!"LIVE".equalsIgnoreCase(auction.getStatus())) continue;

                // Skip if a bot reaction is already waiting for this auction
                if (botService.isAuctionAlreadyScheduled(auction.getId())) {
                    System.out.println("🤖 Auction already scheduled for bot reaction: " + auction.getId());
                    continue;
                }

                // Higher chance so project demo looks active
                int chance = random.nextInt(100);
                if (chance > 80) { // 80% chance to trigger
                    System.out.println("🤖 Skipped by random chance for auction: " + auction.getId());
                    continue;
                }

                System.out.println("🤖 Scheduler triggered bot for auction " + auction.getId());
                botService.triggerBotReaction(auction.getId(), null);

            } catch (Exception e) {
                System.out.println("🤖 Scheduler failed for auction "
                        + auction.getId() + ": " + e.getMessage());
            }
        }
    }
}