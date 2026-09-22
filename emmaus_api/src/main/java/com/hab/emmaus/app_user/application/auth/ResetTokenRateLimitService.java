package com.hab.emmaus.app_user.application.auth;

import com.hab.emmaus.shared.exception.BusinessException;
import com.hab.emmaus.shared.exception.ErrorCode;
import com.hab.emmaus.app_user.domain.reset_token_rate_limit.ResetTokenRateLimit;
import com.hab.emmaus.app_user.domain.app_user.AppUser;
import com.hab.emmaus.app_user.persistence.ResetTokenRateLimitRepository;
import com.hab.emmaus.app_user.persistence.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResetTokenRateLimitService {

    private final ResetTokenRateLimitRepository otpRateLimitRepo;
    private final AppUserRepository userRepo;

    @PreAuthorize("hasRole('ADMIN')")
    public void unblock(String phone) {
        AppUser userIdentity = userRepo.findByEmail(phone)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));
        ResetTokenRateLimit limit = otpRateLimitRepo.findByUserId(userIdentity.getId())
                .orElseThrow();

        limit.resetViolations();
    }

}
