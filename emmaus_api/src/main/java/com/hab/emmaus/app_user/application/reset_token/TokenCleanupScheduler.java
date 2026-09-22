package com.hab.emmaus.app_user.application.reset_token;

import com.hab.emmaus.app_user.persistence.ResetTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class TokenCleanupScheduler {

    private final ResetTokenRepository tokenRepository;

    public TokenCleanupScheduler(ResetTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void purgeExpiredTokens() {
        tokenRepository.deleteAllExpiredOrUsedBefore(Instant.now());
    }
}
