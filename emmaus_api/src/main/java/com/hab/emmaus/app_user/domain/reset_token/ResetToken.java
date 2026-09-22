package com.hab.emmaus.app_user.domain.reset_token;


import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;


@Getter
@Table("reset_token")
public class ResetToken {

    @Id
    private  Long id;
    private Long userId;

    private String tokenHash;

    private Instant expiresAt;

    private boolean used = false;

    private int resendCount;
    private Instant lastSentAt;

    private int attempts;


    private static final int MAX_ATTEMPTS = 5;

    // constructors
    @PersistenceCreator
    private ResetToken(
            Long id,
            Long userId,
            String tokenHash,
            Boolean used,
            Instant expiresAt,
            Integer resendCount,
            Instant lastSentAt,
            int attempts
    ){
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.used = used;
        this.expiresAt = expiresAt;
        this.resendCount = resendCount;
        this.lastSentAt = lastSentAt;
        this.attempts = attempts;
    }

    private ResetToken(
            Long userId,
            String tokenHash,
            Instant expiresAt
    ) {
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.attempts = 0;
        this.lastSentAt = Instant.now();
    }

    //factory method
    public static ResetToken create(
            Long userId,
            String tokenHash,
            Instant expiresAt
    ) {
        return new ResetToken(
                userId,
                tokenHash,
                expiresAt
        );
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }

    public void markAsUsed() {
        this.used = true;
    }

}

