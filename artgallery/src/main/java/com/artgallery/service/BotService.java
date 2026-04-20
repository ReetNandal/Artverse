package com.artgallery.service;

import com.artgallery.model.Auction;
import com.artgallery.model.Bid;
import com.artgallery.model.User;
import com.artgallery.repository.AuctionRepository;
import com.artgallery.repository.BidRepository;
import com.artgallery.repository.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BotService {

    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final BidService bidService;
    private final DynamicBotService dynamicBotService;
    private final WalletService walletService;

    private final Random random = new Random();

    // Keeps track of auctions already waiting for bot reaction
    private final Set<Long> scheduledAuctions = ConcurrentHashMap.newKeySet();

    public BotService(UserRepository userRepository,
                      AuctionRepository auctionRepository,
                      BidRepository bidRepository,
                      BidService bidService,
                      DynamicBotService dynamicBotService,
                      WalletService walletService) {
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.bidService = bidService;
        this.dynamicBotService = dynamicBotService;
        this.walletService = walletService;
    }

    public boolean isAuctionAlreadyScheduled(Long auctionId) {
        return scheduledAuctions.contains(auctionId);
    }

    @Async
    public void triggerBotReaction(Long auctionId, User lastHumanBidder) {

        if (!scheduledAuctions.add(auctionId)) {
            System.out.println("🤖 Bot already scheduled for auction " + auctionId);
            return;
        }

        try {
            // ⏱️ 1.5–2 mins delay
            int delay = 90000 + random.nextInt(30000);
            System.out.println("🤖 Bot may react in " + (delay / 1000) + " sec for auction " + auctionId);
            Thread.sleep(delay);

            Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
            if (auctionOpt.isEmpty()) return;

            Auction auction = auctionOpt.get();
            if (!"LIVE".equalsIgnoreCase(auction.getStatus())) return;

            // 🔥 High response chance
            if (random.nextInt(100) > 85) {
                System.out.println("🤖 No bot reacted for auction " + auctionId);
                return;
            }

            Optional<Bid> topBidOpt =
                    bidRepository.findTop1ByAuction_IdOrderByAmountDescBidTimeDesc(auctionId);

            User currentTopBidder = topBidOpt.map(Bid::getUser).orElse(null);

            List<User> bots = userRepository.findByIsBotTrueAndBotActiveTrue();

            // Create bot if none exist
            if (bots.isEmpty()) {
                User newBot = dynamicBotService.createDynamicBotIfNeeded();
                ensureBotWallet(newBot);
                bots = new ArrayList<>();
                bots.add(newBot);
                System.out.println("🤖 Created bot: " + newBot.getUsername());
            }

            // Ensure wallet for all bots
            for (User bot : bots) {
                ensureBotWallet(bot);
            }

            List<User> eligibleBots = new ArrayList<>();

            for (User bot : bots) {
                if (bot.getId() == null) continue;
                if (bot.getBotMaxBidLimit() == null) continue;
                if (currentTopBidder != null && bot.getId().equals(currentTopBidder.getId())) continue;

                if (bot.getBotMaxBidLimit() > auction.getCurrentBid()) {
                    eligibleBots.add(bot);
                }
            }

            if (eligibleBots.isEmpty()) {
                System.out.println("🤖 No eligible bots for auction " + auctionId);
                return;
            }

            User selectedBot = eligibleBots.get(random.nextInt(eligibleBots.size()));

            int jumpSteps = 1 + random.nextInt(3);
            double nextBid = auction.getCurrentBid() + (auction.getBidIncrement() * jumpSteps);

            if (nextBid > selectedBot.getBotMaxBidLimit()) return;

            bidService.placeBidFromBot(auctionId, nextBid, selectedBot);

            System.out.println("🤖 Bot " + selectedBot.getUsername()
                    + " placed bid ₹" + nextBid);

            // 🔥 Trigger bidding war
            triggerBiddingWarIfNeeded(auctionId, selectedBot);

        } catch (Exception e) {
            System.out.println("🤖 Bot error: " + e.getMessage());
        } finally {
            scheduledAuctions.remove(auctionId);
        }
    }

    private void triggerBiddingWarIfNeeded(Long auctionId, User previousBot) {
        try {
            if (random.nextInt(100) > 35) return;

            Thread.sleep(8000 + random.nextInt(7000));

            Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
            if (auctionOpt.isEmpty()) return;

            Auction auction = auctionOpt.get();
            if (!"LIVE".equalsIgnoreCase(auction.getStatus())) return;

            List<User> bots = userRepository.findByIsBotTrueAndBotActiveTrue();
            List<User> challengers = new ArrayList<>();

            for (User bot : bots) {
                if (bot.getId() == null) continue;
                if (previousBot.getId().equals(bot.getId())) continue;
                if (bot.getBotMaxBidLimit() == null) continue;

                ensureBotWallet(bot);

                if (bot.getBotMaxBidLimit() > auction.getCurrentBid()) {
                    challengers.add(bot);
                }
            }

            if (challengers.isEmpty()) return;

            User challenger = challengers.get(random.nextInt(challengers.size()));

            double nextBid = auction.getCurrentBid() + auction.getBidIncrement();

            if (nextBid > challenger.getBotMaxBidLimit()) return;

            bidService.placeBidFromBot(auctionId, nextBid, challenger);

            System.out.println("🔥 BOT WAR → " + challenger.getUsername()
                    + " countered ₹" + nextBid);

        } catch (Exception e) {
            System.out.println("🤖 War failed: " + e.getMessage());
        }
    }

    // ✅ FIXED METHOD (NO ERROR NOW)
    private void ensureBotWallet(User bot) {
        if (bot == null || bot.getId() == null) return;

        try {
            // Only top-up if balance is low
            if (walletService.getAvailableBalance(bot.getId().intValue())
                    .compareTo(new BigDecimal("1000000")) < 0) {

                walletService.deposit(
                        bot.getId().intValue(),
                        new BigDecimal("50000000.00")
                );

                System.out.println("💰 Bot wallet filled: " + bot.getUsername());
            }

        } catch (Exception e) {
            System.out.println("💰 Wallet skip for "
                    + bot.getUsername() + ": " + e.getMessage());
        }
    }
}