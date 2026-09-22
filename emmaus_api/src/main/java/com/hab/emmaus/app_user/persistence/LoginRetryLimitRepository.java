package com.hab.emmaus.app_user.persistence;

import com.hab.emmaus.app_user.domain.login_retry_limit.LoginRetryLimit;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoginRetryLimitRepository extends CrudRepository<LoginRetryLimit,Long> {

    @Query("""
            SELECT * FROM login_retry_limit 
            WHERE user_id = :userId
            """)
    Optional<LoginRetryLimit> findByUser (Long userId);


    @Query("""
        INSERT INTO login_retry_limit (user_id, window_start, sent_count, violation_count)
        VALUES (:userId, CURRENT_DATE, 0, 0)
        ON CONFLICT (user_id) 
        DO UPDATE SET user_id = login_retry_limit.user_id
        RETURNING *
    """)
    LoginRetryLimit getOrCreate(@Param("userId") Long userId);

}
