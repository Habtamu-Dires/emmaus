package com.hab.emmaus.app_user.application.auth;

import com.hab.emmaus.app_user.application.auth.dto.*;
import com.hab.emmaus.app_user.application.reset_token.ResetService;
import com.hab.emmaus.app_user.domain.app_user.AppUser;
import com.hab.emmaus.app_user.domain.app_user.enums.UserStatus;
import com.hab.emmaus.app_user.domain.login_retry_limit.LoginRetryLimit;
import com.hab.emmaus.app_user.domain.refresh_token.RefreshToken;
import com.hab.emmaus.app_user.persistence.LoginRetryLimitRepository;
import com.hab.emmaus.app_user.persistence.RefreshTokenRepository;
import com.hab.emmaus.app_user.persistence.AppUserRepository;
import com.hab.emmaus.app_user.utils.TokenHasher;
import com.hab.emmaus.infrastructure.security.JwtService;
import com.hab.emmaus.infrastructure.security.SecurityUtils;
import com.hab.emmaus.shared.exception.BusinessException;
import com.hab.emmaus.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository userRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final JwtService jwtService;
    private final ResetService resetService;
    private final PasswordEncoder passwordEncoder;
    private final LoginRetryLimitRepository loginRateLimitRepo;
    private final LoginRateLimitService loginRateLimitService;

    //login
    @Transactional
    public AuthTokens login(AuthenticationRequest request) {

        AppUser user = userRepo.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_CREDENTIALS));


        // check login retry limit
        loginRateLimitRepo.findByUser(user.getId())
                .ifPresent((limit)->limit.checkAllowed(Instant.now()));

        verifyPassword(request.password(), user, true);
        verifyUserStatus(user);


        String accessToken = jwtService.generateAccessToken(
                user.getPublicId(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet())
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.getPublicId()
        );

        String refreshTokenHash = TokenHasher.hash(refreshToken);

        //revoke
        refreshTokenRepo.revokeAllByUserId(user.getId());

        RefreshToken storedRefreshToken = RefreshToken.initial(
                user.getId(),
                refreshTokenHash
        );

        refreshTokenRepo.save(storedRefreshToken);

        return new AuthTokens(accessToken, refreshToken,user.getStatus(), user.getIsTempPassword());
    }

    // refresh
    @Transactional
    public AuthTokens refresh(RefreshRequest req) {

        String hash = TokenHasher.hash(req.refreshToken());

        RefreshToken current = refreshTokenRepo.findByTokenHash(hash)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        AppUser user = userRepo.findById(current.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        //verify user status
        verifyUserStatus(user);

        if (current.isRevoked()) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_REVOKED);
        }

        if(current.isExpired()){
           throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        if (current.isAbsoluteExpired()) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_ABSOLUTE_EXPIRED);
        }

        String newRawToken = jwtService.generateRefreshToken(
                user.getPublicId()
        );

        RefreshToken rotated = current.rotate(
                TokenHasher.hash(newRawToken)
        );

        refreshTokenRepo.save(current);   // revoked
        refreshTokenRepo.save(rotated);   // new token


        String accessToken = jwtService.generateAccessToken(
                user.getPublicId(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet())
        );

        return new AuthTokens(accessToken, newRawToken,user.getStatus(), false);
    }

    // logout only the current session.
    public void logout(LogoutRequest request){
        String hash = TokenHasher.hash(request.refreshToken());
        UUID userPublicId = SecurityUtils.currentUser().userPublicId();

        refreshTokenRepo.findByTokenHash(hash)
                .ifPresent(token -> {
                    RefreshToken refreshToken = token.withRevoked();
                    refreshTokenRepo.save(refreshToken);
                });

    }

    public void logout(){
        UUID userPublicId = SecurityUtils.currentUser().userPublicId();


        AppUser user = userRepo.findByPublicId(userPublicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        refreshTokenRepo.revokeAllByUserId(user.getId());

    }

    // forget password ...
    @Transactional
    public void requestPasswordReset(ForgetPasswordRequest req) {
        resetService.createAndSendPasswordResetToken(req.email());
    }

    // reset password
    public void resetPassword(ResetPasswordRequest req){
        checkPasswords(req.newPassword(), req.confirmPassword());
        resetService.resetPassword(req.rawToken(), req.newPassword());
    }


    // change password
    public void changePassword(ChangePasswordRequest req) {

        AppUser user = userRepo.findByEmail(req.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        verifyPassword(req.currentPassword(), user, false);
        String newHash = passwordEncoder.encode(req.newPassword());
        user.setPasswordAsNotTemp();
        user.changePassword(newHash);
        userRepo.save(user);
    }

    /** helper methods ** */
    private void verifyPassword(
            String loginPassword, AppUser user, boolean checkLimit
    ){
        if (!passwordEncoder.matches(
                loginPassword,
                user.getPasswordHash())
        ) {
            if(checkLimit){
                loginRateLimitService.recordFailedLoginAttempt(user.getId());
            }
            throw new BusinessException(ErrorCode.BAD_CREDENTIALS);
        }
    }

    private void verifyUserStatus(AppUser user){
        if (!user.getIsEmailVerified()) {
            throw new BusinessException(ErrorCode.PHONE_NOT_VERIFIED);
        }

        if (user.getStatus() == UserStatus.REJECTED) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_DEACTIVATED);
        }

        if(user.getStatus() == UserStatus.DISABLED){
            throw  new BusinessException(ErrorCode.ACCOUNT_ALREADY_DEACTIVATED);
        }

    }

    private void checkPasswords(final String password,
                                final String confirmPassword) {
        if (password == null || !password.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }
    }


}
