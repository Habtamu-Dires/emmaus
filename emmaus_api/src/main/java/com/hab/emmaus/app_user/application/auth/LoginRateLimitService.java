package com.hab.emmaus.app_user.application.auth;

import com.hab.emmaus.app_user.domain.login_retry_limit.LoginRetryLimit;
import com.hab.emmaus.app_user.persistence.LoginRetryLimitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LoginRateLimitService {

    private final LoginRetryLimitRepository loginRateLimitRepo;

    // Must be public, and in a separate class!
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailedLoginAttempt(Long userId) {
        LoginRetryLimit limit = loginRateLimitRepo.getOrCreate(userId);

        limit.recordSend(Instant.now());
        loginRateLimitRepo.save(limit);
    }
}
