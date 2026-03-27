package mx.gob.pjpuebla.trials.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class DateRangeMapper {

    private DateRangeMapper() {
    }

    public static DateTimeRange fromDates(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null) {
            return new DateTimeRange(null, null);
        }

        LocalDate effectiveToDate = toDate != null ? toDate : fromDate;
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime toExclusive = effectiveToDate.plusDays(1).atStartOfDay();
        return new DateTimeRange(from, toExclusive);
    }

    public record DateTimeRange(LocalDateTime from, LocalDateTime toExclusive) {
    }
}
