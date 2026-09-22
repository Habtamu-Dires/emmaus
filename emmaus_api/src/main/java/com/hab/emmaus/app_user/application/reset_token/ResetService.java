package com.hab.emmaus.app_user.application.reset_token;


import com.hab.emmaus.app_user.domain.app_user.AppUser;
import com.hab.emmaus.app_user.domain.app_user.enums.UserStatus;
import com.hab.emmaus.app_user.domain.reset_token.ResetToken;
import com.hab.emmaus.app_user.domain.reset_token_rate_limit.ResetTokenRateLimit;
import com.hab.emmaus.app_user.event.EmailResetEvent;
import com.hab.emmaus.app_user.event.PasswordResetEvent;
import com.hab.emmaus.app_user.persistence.AppUserRepository;
import com.hab.emmaus.app_user.persistence.ResetTokenRepository;
import com.hab.emmaus.app_user.persistence.RefreshTokenRepository;
import com.hab.emmaus.app_user.persistence.ResetTokenRateLimitRepository;
import com.hab.emmaus.app_user.utils.TokenHasher;
import com.hab.emmaus.infrastructure.security.SecurityUtils;
import com.hab.emmaus.shared.exception.BusinessException;
import com.hab.emmaus.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResetService {

    private final ResetTokenRepository resetTokenRepository;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResetTokenRateLimitRepository resetTokenRateLimitRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final RefreshTokenRepository refreshTokenRepo;


    @Value("${app.email.frontend-url}")
    private String frontendUrl;

    // Default token lifetime: 15 minutes
    private static final Duration TOKEN_EXPIRATION = Duration.ofMinutes(15);


    /**
     * Creates a new token, invalidates old ones, and triggers email asynchronously.
     */
    @Transactional
    public void createAndSendPasswordResetToken(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        //rate limit check
        ResetTokenRateLimit limit = resetTokenRateLimitRepo.findByUserId(user.getId())
                .orElseGet(() -> ResetTokenRateLimit.create(user.getId()));

        limit.checkAllowed(Instant.now());
        limit.recordSend(Instant.now());
        resetTokenRateLimitRepo.save(limit);

        // Invalidate previous active tokens for this user
        resetTokenRepository.invalidateAllUnusedTokensForUser(user.getId());

        // Generate raw random token for the email link
        String rawToken = UUID.randomUUID().toString();
        String hashedToken = TokenHasher.hash(rawToken);

        ResetToken resetToken = ResetToken.create(
                user.getId(),
                hashedToken,
                Instant.now().plus(TOKEN_EXPIRATION)
        );

        resetTokenRepository.save(resetToken);

        // Send email with raw token link
        eventPublisher.publishEvent(new PasswordResetEvent(
               user.getEmail(),
                user.getFirstName(),
               rawToken
        ));
    }

    /**
     * Validates raw token and resets the password atomically.
     */
    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        String tokenHash = TokenHasher.hash(rawToken);

        ResetToken resetToken = resetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired password reset token."));

        if (resetToken.isUsed() || resetToken.isExpired()) {
            throw new IllegalArgumentException("Invalid or expired password reset token.");
        }

        // Update password & mark token used
        AppUser user = resetTokenRepository.findUserByResetId(resetToken.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.changePassword(passwordEncoder.encode(newPassword));

        ///
        // if user is temp password, turn it to normal password
        if(user.getIsTempPassword()){
            user.setPasswordAsNotTemp();
        }
        // verify user email not verified yet
        if(user.getStatus() == UserStatus.OTP_SENT){
            user.verifyEmail();
        }
        refreshTokenRepo.revokeAllByUserId(user.getId());
        userRepository.save(user);

        resetToken.markAsUsed();
        resetTokenRepository.save(resetToken);
    }

    @Transactional
    public void resetEmail(String rawToken, String newEmail){
        String tokenHash = TokenHasher.hash(rawToken);

        ResetToken resetToken = resetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired password reset token."));

        if (resetToken.isUsed() || resetToken.isExpired()) {
            throw new IllegalArgumentException("Invalid or expired password reset token.");
        }

        // Update email & mark token used
        AppUser user = resetTokenRepository.findUserByResetId(resetToken.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.changeEmail(newEmail);

        ///
        // if user is temp password, turn it to normal password
        if(user.getIsTempPassword()){
            user.setPasswordAsNotTemp();
        }
        // verify user email not verified yet
        user.verifyEmail();
        refreshTokenRepo.revokeAllByUserId(user.getId());
        userRepository.save(user);

        resetToken.markAsUsed();
        resetTokenRepository.save(resetToken);
    }

    @Transactional
    public void updateEmail(String newEmail){
        UUID userId = SecurityUtils.currentUser().userPublicId();
        AppUser user = userRepository.findByPublicId(userId)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(user.getEmail().equals(newEmail)){
            return;
        }

        // Generate raw random token for the email link
        String rawToken = UUID.randomUUID().toString();
        String hashedToken = TokenHasher.hash(rawToken);

        ResetToken resetToken = ResetToken.create(
                user.getId(),
                hashedToken,
                Instant.now().plus(TOKEN_EXPIRATION)
        );

        resetTokenRepository.save(resetToken);

        //send confirmation email
        eventPublisher.publishEvent(new EmailResetEvent(
                newEmail,
                user.getFirstName(),
                rawToken
        ));
    }

}
