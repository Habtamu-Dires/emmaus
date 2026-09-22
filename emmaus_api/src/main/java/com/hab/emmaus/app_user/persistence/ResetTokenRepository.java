package com.hab.emmaus.app_user.persistence;

import com.hab.emmaus.app_user.domain.app_user.AppUser;
import com.hab.emmaus.app_user.domain.reset_token.ResetToken;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.Optional;

public interface ResetTokenRepository extends CrudRepository<ResetToken, Long> {



    @Modifying
    @Query("""
            DELETE FROM reset_token p 
            WHERE p.expiryDate < :now OR p.used = true
            """)
    void deleteAllExpiredOrUsedBefore(Instant now);

    @Modifying
    @Query("""
            UPDATE reset_token SET used = true 
            WHERE user_id = :userId AND used = false
            """)
    void invalidateAllUnusedTokensForUser(long userId);

    @Query("""
            SELECT * FROM reset_token
            WHERE token_hash = :tokenHash
            """)
    Optional<ResetToken> findByTokenHash(String tokenHash);

    @Query("""
            SELECT u.* FROM reset_token rt
            JOIN app_user u ON u.id = rt.user_id
            WHERE rt.id = :resetTokenId
            """)
    Optional<AppUser> findUserByResetId(Long resetTokenId);
}
