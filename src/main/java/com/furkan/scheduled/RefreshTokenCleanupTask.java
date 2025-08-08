package com.furkan.scheduled;

import com.furkan.repository.RefreshTokenRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class RefreshTokenCleanupTask {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @PostConstruct
    public void initCleanUp() {
        int deletedCount = refreshTokenRepository.deleteAllExpiredTokens();
        System.out.println("Startup cleanup - silinen expired refresh token sayısı: " + deletedCount);
    }

    @Scheduled(cron = "0 0 * * * *")
    public void cleanExpiredTokens() {
        int deletedCount = refreshTokenRepository.deleteAllExpiredTokens();
        System.out.println("Silinen expired refresh token sayısı: " + deletedCount);
    }
}
