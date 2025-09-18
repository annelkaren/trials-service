package mx.gob.pjpuebla.trials.statistics.reports;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReporteRecord(
        String key,
        String name,
        String description,
        LocalDate date
) implements Serializable {
}