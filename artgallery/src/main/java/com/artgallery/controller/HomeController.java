package com.artgallery.controller;

import com.artgallery.model.Auction;
import com.artgallery.model.Bid;
import com.artgallery.model.User;
import com.artgallery.model.Wallet;
import com.artgallery.service.AuctionService;
import com.artgallery.service.BidService;
import com.artgallery.service.UserService;
import com.artgallery.service.WalletService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    private final AuctionService auctionService;
    private final BidService bidService;
    private final UserService userService;
    private final WalletService walletService;

    public HomeController(AuctionService auctionService,
                          BidService bidService,
                          UserService userService,
                          WalletService walletService) {
        this.auctionService = auctionService;
        this.bidService = bidService;
        this.userService = userService;
        this.walletService = walletService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/auctions";
    }

    @GetMapping("/auctions")
    public String listAuctions(
            @RequestParam(required = false, defaultValue = "LIVE") String status,
            @RequestParam(required = false) String category,
            Model model) {

        List<Auction> auctions = auctionService.getAuctionsByFilters(status, category);

        model.addAttribute("auctions", auctions);
        model.addAttribute("status", status);
        model.addAttribute("category", category);
        model.addAttribute("categories", auctionService.getAllCategories());
        model.addAttribute("heatmapData", auctionService.getDynamicHeatmap());

        return "auctions";
    }

    @GetMapping("/auction/{id}")
    public String openBidNowPage(@PathVariable Long id, Model model, Principal principal) {
        Auction auction = auctionService.getAuctionById(id);
        List<Bid> recentBids = bidService.getRecentBids(id);

        model.addAttribute("auction", auction);
        model.addAttribute("recentBids", recentBids);

        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            Wallet wallet = walletService.getOrCreateWallet(user.getId().intValue());
            model.addAttribute("wallet", wallet);
            model.addAttribute("availableBalance", wallet.getAvailableBalance());
        }

        return "bidnow";
    }
}