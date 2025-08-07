package mx.gob.pjpuebla.trials.statistics.reports;

import com.fasterxml.jackson.annotation.JsonInclude;
import mx.gob.pjpuebla.trials.core.personas.PersonaRecord;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReporteDatesRecord(
        String reporte,
        LocalDate date
) implements Serializable {
}