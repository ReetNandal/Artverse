package com.artgallery.controller;

import com.artgallery.dto.ListingForm;
import com.artgallery.model.Auction;
import com.artgallery.model.CollectionItem;
import com.artgallery.model.User;
import com.artgallery.model.Wallet;
import com.artgallery.model.WalletTransaction;
import com.artgallery.repository.AuctionRepository;
import com.artgallery.repository.BidRepository;
import com.artgallery.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.util.List;
import com.artgallery.model.ShippingAddress;
import com.artgallery.service.ShippingAddressService;

@Controller
public class UserAccountController {

    private final UserService userService;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final AuctionService auctionService;
    private final WalletService walletService;
    private final ArtworkService artworkService;
    private final CollectionService collectionService;
    private final ListingService listingService;
    private final ShippingAddressService shippingAddressService;
    public UserAccountController(
            UserService userService,
            AuctionRepository auctionRepository,
            BidRepository bidRepository,
            AuctionService auctionService,
            WalletService walletService,
            ArtworkService artworkService,
            CollectionService collectionService,
            ListingService listingService,
            ShippingAddressService shippingAddressService
    ) {
        this.userService = userService;
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.auctionService = auctionService;
        this.walletService = walletService;
        this.artworkService = artworkService;
        this.collectionService = collectionService;
        this.listingService = listingService;
        this.shippingAddressService = shippingAddressService;
    }

    @GetMapping("/account")
    public String accountPage(
            @RequestParam(defaultValue = "dashboard") String section,
            @RequestParam(required = false) Long checkoutAuctionId,
            @RequestParam(required = false) Long detailAuctionId,
            Model model,
            Principal principal
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);

        Wallet wallet = walletService.getOrCreateWallet(user.getId().intValue());
        List<WalletTransaction> txns = walletService.getRecentTransactions(wallet.getId());

        model.addAttribute("wallet", wallet);
        model.addAttribute("walletTxns", txns);
        model.addAttribute("totalAdded", walletService.getTotalAdded(wallet.getId()));
        model.addAttribute("totalSpent", walletService.getTotalSpent(wallet.getId()));
        model.addAttribute("walletBalance", wallet.getBalance());

        long bidsCount = bidRepository.countByUser_Id(user.getId());
        long purchasedCount = auctionRepository.countByWinnerId(user.getId());

        model.addAttribute("bidsCount", bidsCount);
        model.addAttribute("purchasedCount", purchasedCount);

        List<Auction> recentPurchases = auctionRepository.findRecentWins(user.getId(), 2);
        recentPurchases = recentPurchases.stream()
                .map(a -> auctionService.getAuctionById(a.getId()))
                .toList();
        model.addAttribute("recentPurchases", recentPurchases);

        List<Auction> activeListings = auctionRepository.findActiveAuctionsBySellerLimited(user.getId(), 2);
        activeListings = activeListings.stream()
                .map(a -> auctionService.getAuctionById(a.getId()))
                .toList();
        model.addAttribute("activeListings", activeListings);

        model.addAttribute("listingCount", auctionRepository.countBySellerId(user.getId()));
        model.addAttribute("collectionCategories", artworkService.getAllCategories());

        List<CollectionItem> myCollection = collectionService.getMyCollection(user.getId(), null, null, "latest");
        model.addAttribute("myCollection", myCollection);

        model.addAttribute("myListings", listingService.getMyListings(user.getId()));
        model.addAttribute("listingForm", new ListingForm());

        List<Auction> resultAuctions = auctionService.getResultAuctionsForUser(user.getId());
        model.addAttribute("resultAuctions", resultAuctions);
        model.addAttribute("pendingResultsCount",
                resultAuctions.stream().filter(a -> "PAYMENT_PENDING".equals(a.getResultStatus())).count());

        ShippingAddress shippingAddress = shippingAddressService.getByUserId(user.getId());
        if (shippingAddress == null) {
            shippingAddress = new ShippingAddress();
        }
        model.addAttribute("shippingAddress", shippingAddress);

        if (checkoutAuctionId != null) {
            Auction checkoutAuction = auctionService.getCheckoutAuction(checkoutAuctionId, user.getId());
            model.addAttribute("checkoutAuction", checkoutAuction);
        }

        if (detailAuctionId != null) {
            Auction detailAuction = auctionService.getCheckoutAuction(detailAuctionId, user.getId());
            model.addAttribute("detailAuction", detailAuction);
        }

        model.addAttribute("activeSection", section);
        return "useraccount";
    }

    @PostMapping("/account/listings/create")
    public String createListing(@ModelAttribute ListingForm listingForm,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            listingService.createListing(principal.getName(), listingForm);
            redirectAttributes.addFlashAttribute("listingSuccess", "Listing created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("listingError", e.getMessage());
        }

        return "redirect:/account?section=listings";
    }
    @PostMapping("/account/result/pay")
    public String payForWonAuction(@RequestParam Long auctionId,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName());
        Auction auction = auctionService.getCheckoutAuction(auctionId, user.getId());

        if ("EXPIRED".equals(auction.getResultStatus())) {
            redirectAttributes.addFlashAttribute("listingError", "Payment window has expired.");
            return "redirect:/account?section=result";
        }

        auctionService.markAuctionPaid(auction);
        redirectAttributes.addFlashAttribute("listingSuccess", "Payment completed successfully.");
        return "redirect:/account?section=result&detailAuctionId=" + auctionId;
    }
    @PostMapping("/account/shipping/save")
    public String saveShippingAddress(@ModelAttribute ShippingAddress shippingAddress,
                                      Principal principal,
                                      RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            User user = userService.findByUsername(principal.getName());

            ShippingAddress existingAddress = shippingAddressService.getByUserId(user.getId());

            if (existingAddress != null) {
                existingAddress.setFullName(shippingAddress.getFullName());
                existingAddress.setPhone(shippingAddress.getPhone());
                existingAddress.setAddressLine1(shippingAddress.getAddressLine1());
                existingAddress.setAddressLine2(shippingAddress.getAddressLine2());
                existingAddress.setCity(shippingAddress.getCity());
                existingAddress.setState(shippingAddress.getState());
                existingAddress.setPostalCode(shippingAddress.getPostalCode());
                existingAddress.setCountry(shippingAddress.getCountry());

                shippingAddressService.save(existingAddress);
            } else {
                shippingAddress.setUserId(user.getId());
                shippingAddressService.save(shippingAddress);
            }

            redirectAttributes.addFlashAttribute("listingSuccess", "Shipping address saved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("listingError", "Failed to save shipping address.");
        }

        return "redirect:/account?section=shipping";
    }
}