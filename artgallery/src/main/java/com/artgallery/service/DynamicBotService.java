package com.artgallery.service;

import com.artgallery.model.User;
import com.artgallery.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class DynamicBotService {

    private final UserRepository userRepository;
    private final Random random = new Random();

    public DynamicBotService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createDynamicBotIfNeeded() {
        String username = "bot_" + System.currentTimeMillis();

        User bot = new User();
        bot.setUsername(username);
        bot.setEmail(username + "@artverse.com");
        bot.setPassword("123");
        bot.setFirstName("Auto");
        bot.setLastName("Bot");
        bot.setCreatedAt(LocalDateTime.now());
        bot.setIsActive(true);
        bot.setIsBot(true);
        bot.setBotActive(true);

        double minLimit = 9000000.0;
        double maxExtra = 3000000.0;
        bot.setBotMaxBidLimit(minLimit + random.nextDouble() * maxExtra);

        return userRepository.save(bot);
    }
}