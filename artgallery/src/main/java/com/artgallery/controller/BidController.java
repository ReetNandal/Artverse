package com.artgallery.controller;

import com.artgallery.model.User;
import com.artgallery.repository.UserRepository;
import com.artgallery.service.BidService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class BidController {

    private final BidService bidService;
    private final UserRepository userRepository;

    public BidController(BidService bidService, UserRepository userRepository) {
        this.bidService = bidService;
        this.userRepository = userRepository;
    }

    @PostMapping("/bids")
    public String placeBid(@RequestParam Long auctionId,
                           @RequestParam Double amount,
                           Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));

        bidService.placeBid(auctionId, amount, user);

        return "redirect:/auction/" + auctionId;
    }
}