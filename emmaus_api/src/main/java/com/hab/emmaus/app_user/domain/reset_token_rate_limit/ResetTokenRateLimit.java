package com.hab.emmaus.app_user.domain.reset_token_rate_limit;

import com.hab.emmaus.shared.exception.BusinessException;

import com.hab.emmaus.app_user.utils.TimeFormatUtil;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static com.hab.emmaus.shared.exception.ErrorCode.OTP_BLOCKED;


@Table("reset_token_rate_limit")
public class ResetTokenRateLimit {

    @Id
    private Long id;

    private Long userId;

    private LocalDate windowStart;   // usually today
    private int sentCount;

    private int violationCount;      // escalations

    private Instant blockedUntil;

    public ResetTokenRateLimit(){}

    /* ---------- Domain rules ---------- */
    @PersistenceCreator
    public ResetTokenRateLimit(
            Long id,
            Long userId,
            LocalDate windowStart,
            int sentCount,
            int violationCount,
            Instant blockedUntil
    ){
        this.id = id;
        this.userId = userId;
        this.windowStart = windowStart;
        this.sentCount = sentCount;
        this.violationCount = violationCount;
        this.blockedUntil = blockedUntil;
    }

    public static ResetTokenRateLimit create(Long userId) {
        ResetTokenRateLimit limit = new ResetTokenRateLimit();
        limit.userId = userId;
        limit.windowStart = LocalDate.now();
        limit.violationCount = 0;
        limit.blockedUntil = null;
        limit.sentCount = 1;
        return limit;
    }

    public void checkAllowed(Instant now) {
        if (blockedUntil != null && now.isBefore(blockedUntil)) {
            String blockedTime =
                    TimeFormatUtil.formatToMinute(blockedUntil);
            throw new BusinessException(
                    OTP_BLOCKED,
                    blockedTime
            );
        }
    }

    public void recordSend(Instant now) {
        resetWindowIfNeeded(now);

        sentCount++;

        if (sentCount > 5) { // example daily limit
            escalateBlock(now);
        }
    }

    private void resetWindowIfNeeded(Instant now) {
        ZoneId zoneId = TimeFormatUtil.zoneId;
        LocalDate today = now.atZone(zoneId).toLocalDate();
        if (!today.equals(windowStart)) {
            windowStart = today;
            sentCount = 1;
        }
    }

    private void escalateBlock(Instant now) {
        violationCount++;

        long blockDays = calculateBlockDays(violationCount);

//        blockedUntil = now.plusDays(blockDays);
        blockedUntil = now.plus(Duration.ofDays(blockDays));
        sentCount = 0;
    }

    private long calculateBlockDays(int violationCount) {
        // 1 → 1 day, 2 → 2 days, 3 → 4 days
        return Math.min(1L << (violationCount - 1), 30);
    }

    public void resetViolations() {
        this.violationCount = 0;
        this.blockedUntil = null;
    }

}