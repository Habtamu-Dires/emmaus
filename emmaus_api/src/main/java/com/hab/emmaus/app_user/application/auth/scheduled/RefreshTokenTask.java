package com.hab.emmaus.app_user.application.auth.scheduled;

import com.hab.emmaus.app_user.persistence.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenTask {

    private final RefreshTokenRepository tokenRepo;

    @Scheduled(cron = "0 0 2 * * *")
    void removeRevokedAndExpiredTokens(){
        log.info("Removing revoked or expired tokens");
        tokenRepo.deleteRevokedAndExpiredTokens();
    }
}
