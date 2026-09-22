package com.hab.emmaus.app_user.domain.login_retry_limit;

import com.hab.emmaus.app_user.utils.TimeFormatUtil;
import com.hab.emmaus.shared.exception.BusinessException;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static com.hab.emmaus.shared.exception.ErrorCode.LOGIN_BLOCKED;


@Table("login_retry_limit")
public class LoginRetryLimit {

    @Id
    private Long id;
    private Long userId;

    private LocalDate windowStart;   // usually today
    private int sentCount;

    private int violationCount;      // escalations

    private Instant blockedUntil;

    public LoginRetryLimit(){}

    /* ---------- Domain rules ---------- */
    @PersistenceCreator
    public LoginRetryLimit(
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

    public static LoginRetryLimit create(Long userId) {
        LoginRetryLimit limit = new LoginRetryLimit();
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
                    LOGIN_BLOCKED,
                    blockedTime
            );
        }
    }

    public void recordSend(Instant now) {
        resetWindowIfNeeded(now);

        sentCount++;

        if (sentCount > 5) { // example minute limit
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
        blockedUntil = now.plus(Duration.ofMinutes(blockDays));
        sentCount = 0;
    }

    private long calculateBlockDays(int violationCount) {
        // 1 → 1 minutes, 2 → 2 minutes, 3 → 4 minutes
        return Math.min(1L << (violationCount - 1), 30);
    }

    public void resetViolations() {
        this.violationCount = 0;
        this.blockedUntil = null;
    }
}
