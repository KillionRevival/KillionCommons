package co.killionrevival.killioncommons.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static DateTimeFormatter HUMAN_READABLE_DATE = DateTimeFormatter.ofPattern("dd MMMM yyyy 'at' hh:mm:ss");

    /**
     * Returns a String in an example format of:
     * Uses system default ZoneId
     * "(2-digit Month) (Month in English) (4-digit Year) at (hh:mm:ss)" (military time)
     * @param instant Instant to parse into a readable string
     * @return "(2-digit Month) (Month in English) (4-digit Year) at (hh:mm:ss)" (military time)
     */
    public static String getHumanReadableDateTimeString(
            final Instant instant
    ) {
        return getHumanReadableDateTimeString(instant, ZoneId.systemDefault());
    }

    /**
     * Returns a String in an example format of:
     * "(2-digit Month) (Month in English) (4-digit Year) at (hh:mm:ss)" (military time)
     * @param instant Instant to parse into a readable string
     * @return "(2-digit Month) (Month in English) (4-digit Year) at (hh:mm:ss)" (military time)
     */
    public static String getHumanReadableDateTimeString(
            final Instant instant,
            final ZoneId timeZone
    ) {
        return HUMAN_READABLE_DATE.format(instant.atZone(timeZone));
    }

    /**
     * Gets a Duration from a string of characters that are common in Spigot plugins.
     * Valid strings include:
     * d - Day
     * h - Hour
     * m - Minute
     * s - Second
     * mo - Month
     * w - Week
     * Month and week are added into the duration as 30 days and 7 days respectively. There is no native operation for
     * adding weeks or months. I think you could get away with .5w or something, but that is unsupported! Good luck with
     * that!
     * @param timeString String representing the duration with valid characters as described above
     * @return A duration matching the time in the String, or ZERO if parsing fails.
     * @throws IllegalArgumentException if splitting the string fails, or if the numbers in the string can't be parsed into
     * a long.
     */
    public static Duration getDurationFromTimeString(final String timeString) throws IllegalArgumentException {
        if (timeString == null || timeString.isEmpty()) {
            return Duration.ZERO;
        }
        // format is 1d2h3m4s
        final String[] timeParts = timeString.split("(?<=\\D)(?=\\d|-\\d)|(?<=\\d)(?=\\D)");
        Duration duration = Duration.ZERO;
        boolean isNegative = false;
        Long timeValue = null;

        for (final String token : timeParts) {
            if (token.equals("-")) {
                isNegative = true;
                continue;
            }
            if (timeValue == null) {
                timeValue = Long.parseLong(token);
                continue;
            }
            duration = switch (token) {
                case "d" -> isNegative ? duration.minusDays(timeValue) : duration.plusDays(timeValue);
                case "h" -> isNegative ? duration.minusHours(timeValue) : duration.plusHours(timeValue);
                case "m" -> isNegative ? duration.minusMinutes(timeValue) : duration.plusMinutes(timeValue);
                case "s" -> isNegative ? duration.minusSeconds(timeValue) : duration.plusSeconds(timeValue);
                case "mo" -> isNegative ? duration.minusDays(30*timeValue) : duration.plusDays(30*timeValue);
                case "w" -> isNegative ? duration.minusDays(7*timeValue) : duration.plusDays(7*timeValue);
                default -> throw new IllegalArgumentException("Invalid time unit: " + token);
            };

            isNegative = false;
            timeValue = null;
        }

        return duration;
    }

    /**
     * Returns a string in the format common for plugins, in days/hours/minutes/seconds
     * Example: "30d2h5m30s". Can be parsed by {@link DateUtil#getDurationFromTimeString}
     *
     * @param duration Duration to be parsed into a time string
     * @return A string in the format common for plugins, in days/hours/minutes/seconds. Example: "30d2h5m30s".
     */
    public static String getTimeStringFromDuration(final Duration duration) {
        final long days = duration.toDays();
        final long hours = duration.minusDays(days).toHours();
        final long minutes = duration.minusDays(days).minusHours(hours).toMinutes();
        final long seconds = duration.minusDays(days).minusHours(hours).minusMinutes(minutes).getSeconds();
        String output = "";
        if (days > 0) {
            output += days + "d";
        }
        if (hours > 0) {
            output += hours + "h";
        }
        if (minutes > 0) {
            output += minutes + "m";
        }
        if (seconds > 0) {
            output += seconds + "s";
        }

        return output;
    }
}
