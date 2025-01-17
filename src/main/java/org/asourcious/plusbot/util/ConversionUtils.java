package org.asourcious.plusbot.util;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public final class ConversionUtils {
    private ConversionUtils() {}

    public static ChronoUnit convert(TimeUnit unit) {
        switch (unit) {
            case DAYS:
                return ChronoUnit.DAYS;
            case HOURS:
                return ChronoUnit.HOURS;
            case MINUTES:
                return ChronoUnit.MINUTES;
            case SECONDS:
                return ChronoUnit.SECONDS;
            case MICROSECONDS:
                return ChronoUnit.MICROS;
            case MILLISECONDS:
                return ChronoUnit.MILLIS;
            case NANOSECONDS:
                return ChronoUnit.NANOS;
            default:
                return ChronoUnit.FOREVER;
        }
    }

    public static double kelvinToCelcius(double degrees) {
        return degrees - 273.15;
    }

    public static double kelvinToFarenheit(double degrees) {
        return kelvinToCelcius(degrees) * 9/5 + 32;
    }
}
