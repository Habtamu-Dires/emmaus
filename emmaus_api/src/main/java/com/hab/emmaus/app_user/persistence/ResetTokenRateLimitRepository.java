package com.hab.emmaus.app_user.persistence;

import com.hab.emmaus.app_user.domain.reset_token_rate_limit.ResetTokenRateLimit;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ResetTokenRateLimitRepository extends CrudRepository<ResetTokenRateLimit,Long> {

    @Query("""
            SELECT * FROM reset_token_rate_limit 
            WHERE user_id = :userId
            """)
    Optional<ResetTokenRateLimit> findByUserId(Long userId);
}
