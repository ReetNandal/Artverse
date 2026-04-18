package com.artgallery.controller;

import com.artgallery.model.Bid;
import com.artgallery.model.User;
import com.artgallery.model.Wallet;
import com.artgallery.repository.UserRepository;
import com.artgallery.service.BidService;
import com.artgallery.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BidApiController {

    private final BidService bidService;
    private final UserRepository userRepository;
    private final WalletService walletService;

    public BidApiController(BidService bidService,
                            UserRepository userRepository,
                            WalletService walletService) {
        this.bidService = bidService;
        this.userRepository = userRepository;
        this.walletService = walletService;
    }

    @PostMapping("/bids")
    public ResponseEntity<?> placeBid(@RequestParam Long auctionId,
                                      @RequestParam Double amount,
                                      Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Please login"));
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));

        bidService.placeBid(auctionId, amount, user);

        double newCurrentBid = bidService.getCurrentBid(auctionId);
        long totalBids = bidService.getBidCount(auctionId);

        List<Bid> recent = bidService.getRecentBids(auctionId);

        List<Map<String, Object>> recentDto = recent.stream().map(b -> {
            Map<String, Object> m = new HashMap<>();
            m.put("username", b.getUser().getUsername());
            m.put("amount", b.getAmount());
            m.put("bidTime", b.getBidTime().toString());
            return m;
        }).toList();

        Wallet wallet = walletService.getOrCreateWallet(user.getId().intValue());

        return ResponseEntity.ok(Map.of(
                "message", "Bid placed successfully",
                "currentBid", newCurrentBid,
                "totalBids", totalBids,
                "recentBids", recentDto,
                "walletBalance", wallet.getBalance(),
                "heldBalance", wallet.getHeldBalance(),
                "availableBalance", wallet.getAvailableBalance()
        ));
    }

    @GetMapping("/auctions/{id}/bids/recent")
    public ResponseEntity<?> recentBids(@PathVariable Long id) {
        List<Bid> recent = bidService.getRecentBids(id);

        List<Map<String, Object>> recentDto = recent.stream().map(b -> {
            Map<String, Object> m = new HashMap<>();
            m.put("username", b.getUser().getUsername());
            m.put("amount", b.getAmount());
            m.put("bidTime", b.getBidTime().toString());
            return m;
        }).toList();

        return ResponseEntity.ok(Map.of("recentBids", recentDto));
    }
}