package com.hab.emmaus.app_user.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public final class TimeFormatUtil {

    private TimeFormatUtil() {}

    public static final ZoneId zoneId = ZoneId.of("Africa/Addis_Ababa");

    private static final DateTimeFormatter DATE_TIME_MINUTE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final DateTimeFormatter DATE_TIME_SECOND =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter MONTH_NAME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy, MMM dd hh:mm:ss a", Locale.ENGLISH);

    public static String formatToMinute(Instant instant) {
        validate(instant);
        return instant.atZone(zoneId).format(DATE_TIME_MINUTE);
    }

    public static String formatToSecond(Instant instant) {
        validate(instant);
        return instant.atZone(zoneId).format(DATE_TIME_SECOND);
    }

    public static String formatWithMonthName(Instant instant) {
        validate(instant);
        return instant.atZone(zoneId).format(MONTH_NAME_FORMAT);
    }

    private static void validate(Instant instant) {
        Objects.requireNonNull(instant, "instant must not be null");
        Objects.requireNonNull(zoneId, "zoneId must not be null");
    }
}