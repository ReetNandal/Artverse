package com.artgallery.controller;

import com.artgallery.model.User;
import com.artgallery.repository.UserRepository;
import com.artgallery.service.WalletService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.security.Principal;

@Controller
public class WalletController {

    private final WalletService walletService;
    private final UserRepository userRepository;

    public WalletController(WalletService walletService, UserRepository userRepository) {
        this.walletService = walletService;
        this.userRepository = userRepository;
    }

    @PostMapping("/wallet/deposit")
    public String deposit(@RequestParam BigDecimal amount, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));

        walletService.deposit(user.getId().intValue(), amount);

        // ✅ stay on wallet + trigger popup
        return "redirect:/account?tab=wallet&depositSuccess=1";
    }
}