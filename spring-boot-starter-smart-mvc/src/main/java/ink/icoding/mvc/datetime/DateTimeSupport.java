package ink.icoding.mvc.datetime;

import ink.icoding.mvc.entitys.DateTimeConfig;
import ink.icoding.mvc.entitys.IncompleteDateTimePolicy;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * Centralizes temporal parsing, formatting, incomplete-input handling, and zone conversion.
 *
 * <p>Jackson components and Spring MVC formatters delegate to this class so request bodies,
 * query parameters, path variables, and responses follow identical rules. Instant-based types
 * accept standard ISO representations as well as the configured local date-time pattern.</p>
 */
public final class DateTimeSupport {

    private DateTimeSupport() {
    }

    public static LocalDateTime parseLocalDateTime(String value, DateTimeConfig config) {
        DateTimeFormatter primary = DateTimeFormatter.ofPattern(config.getRequestFormat());
        try {
            return LocalDateTime.parse(value, primary);
        } catch (DateTimeParseException exception) {
            if (config.getIncompleteInputPolicy() != IncompleteDateTimePolicy.FILL_MISSING) {
                throw exception;
            }
            return parseDateTimeAndFill(value, exception);
        }
    }

    public static LocalDate parseLocalDate(String value, DateTimeConfig config) {
        DateTimeFormatter primary = DateTimeFormatter.ofPattern(config.getDateRequestFormat());
        try {
            return LocalDate.parse(value, primary);
        } catch (DateTimeParseException exception) {
            if (config.getIncompleteInputPolicy() != IncompleteDateTimePolicy.FILL_MISSING) {
                throw exception;
            }
            return parseDateAndFill(value, exception);
        }
    }

    public static LocalTime parseLocalTime(String value, DateTimeConfig config) {
        DateTimeFormatter primary = DateTimeFormatter.ofPattern(config.getTimeRequestFormat());
        try {
            return LocalTime.parse(value, primary);
        } catch (DateTimeParseException exception) {
            if (config.getIncompleteInputPolicy() != IncompleteDateTimePolicy.FILL_MISSING) {
                throw exception;
            }
            return parseTimeAndFill(value, exception);
        }
    }

    public static Instant parseInstant(String value, DateTimeConfig config) {
        DateTimeFormatter configured = DateTimeFormatter.ofPattern(config.getRequestFormat());
        try {
            return ZonedDateTime.parse(value, configured).toInstant();
        } catch (DateTimeParseException ignored) {
            // Try a configured offset date-time next.
        }
        try {
            return OffsetDateTime.parse(value, configured).toInstant();
        } catch (DateTimeParseException ignored) {
            // Try standard ISO representations next.
        }
        try {
            return Instant.parse(value);
        } catch (DateTimeParseException ignored) {
            // Try an offset or configured local date-time next.
        }
        try {
            return OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant();
        } catch (DateTimeParseException ignored) {
            return parseLocalDateTime(value, config).atZone(resolveZoneId(config)).toInstant();
        }
    }

    public static OffsetDateTime parseOffsetDateTime(String value, DateTimeConfig config) {
        try {
            return OffsetDateTime.parse(value,
                    DateTimeFormatter.ofPattern(config.getRequestFormat()));
        } catch (DateTimeParseException ignored) {
            // Try standard ISO representations next.
        }
        try {
            return OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            // Try an instant or configured local date-time next.
        }
        try {
            return Instant.parse(value).atZone(resolveZoneId(config)).toOffsetDateTime();
        } catch (DateTimeParseException ignored) {
            return parseLocalDateTime(value, config).atZone(resolveZoneId(config))
                    .toOffsetDateTime();
        }
    }

    public static ZonedDateTime parseZonedDateTime(String value, DateTimeConfig config) {
        try {
            return ZonedDateTime.parse(value,
                    DateTimeFormatter.ofPattern(config.getRequestFormat()));
        } catch (DateTimeParseException ignored) {
            // Try a configured offset date-time next.
        }
        try {
            return OffsetDateTime.parse(value,
                    DateTimeFormatter.ofPattern(config.getRequestFormat()))
                    .atZoneSameInstant(resolveZoneId(config));
        } catch (DateTimeParseException ignored) {
            // Try standard ISO representations next.
        }
        try {
            return ZonedDateTime.parse(value, DateTimeFormatter.ISO_ZONED_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            // Try an offset or configured local date-time next.
        }
        try {
            return OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .atZoneSameInstant(resolveZoneId(config));
        } catch (DateTimeParseException ignored) {
            return parseLocalDateTime(value, config).atZone(resolveZoneId(config));
        }
    }

    public static Date parseDate(String value, DateTimeConfig config) {
        return Date.from(parseInstant(value, config));
    }

    public static String format(LocalDateTime value, DateTimeConfig config) {
        return value.format(DateTimeFormatter.ofPattern(config.getResponseFormat()));
    }

    public static String format(LocalDate value, DateTimeConfig config) {
        return value.format(DateTimeFormatter.ofPattern(config.getDateResponseFormat()));
    }

    public static String format(LocalTime value, DateTimeConfig config) {
        return value.format(DateTimeFormatter.ofPattern(config.getTimeResponseFormat()));
    }

    public static String format(Instant value, DateTimeConfig config) {
        return DateTimeFormatter.ofPattern(config.getResponseFormat())
                .withZone(resolveZoneId(config)).format(value);
    }

    public static String format(OffsetDateTime value, DateTimeConfig config) {
        return value.atZoneSameInstant(resolveZoneId(config))
                .format(DateTimeFormatter.ofPattern(config.getResponseFormat()));
    }

    public static String format(ZonedDateTime value, DateTimeConfig config) {
        return value.withZoneSameInstant(resolveZoneId(config))
                .format(DateTimeFormatter.ofPattern(config.getResponseFormat()));
    }

    public static String format(Date value, DateTimeConfig config) {
        return format(value.toInstant(), config);
    }

    public static ZoneId resolveZoneId(DateTimeConfig config) {
        String configured = config.getZoneId();
        if (configured == null || configured.trim().isEmpty()
                || "system-default".equalsIgnoreCase(configured.trim())) {
            return ZoneId.systemDefault();
        }
        return ZoneId.of(configured.trim());
    }

    private static LocalDateTime parseDateTimeAndFill(String value,
                                                       DateTimeParseException original) {
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    .withSecond(0).withNano(0);
        } catch (DateTimeParseException ignored) {
            // Try a date-only value next.
        }
        try {
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        } catch (DateTimeParseException ignored) {
            // Try a year-month value next.
        }
        try {
            return YearMonth.parse(value, DateTimeFormatter.ofPattern("yyyy-MM"))
                    .atDay(1).atStartOfDay();
        } catch (DateTimeParseException ignored) {
            // Try a year-only value next.
        }
        try {
            return Year.parse(value, DateTimeFormatter.ofPattern("yyyy"))
                    .atDay(1).atStartOfDay();
        } catch (DateTimeParseException ignored) {
            throw original;
        }
    }

    private static LocalDate parseDateAndFill(String value, DateTimeParseException original) {
        try {
            return YearMonth.parse(value, DateTimeFormatter.ofPattern("yyyy-MM")).atDay(1);
        } catch (DateTimeParseException ignored) {
            // Try a year-only value next.
        }
        try {
            return Year.parse(value, DateTimeFormatter.ofPattern("yyyy")).atDay(1);
        } catch (DateTimeParseException ignored) {
            throw original;
        }
    }

    private static LocalTime parseTimeAndFill(String value, DateTimeParseException original) {
        try {
            return LocalTime.parse(value, DateTimeFormatter.ofPattern("HH:mm"))
                    .withSecond(0).withNano(0);
        } catch (DateTimeParseException ignored) {
            // Try an hour-only value next.
        }
        try {
            return LocalTime.parse(value, DateTimeFormatter.ofPattern("HH"))
                    .withMinute(0).withSecond(0).withNano(0);
        } catch (DateTimeParseException ignored) {
            throw original;
        }
    }
}
