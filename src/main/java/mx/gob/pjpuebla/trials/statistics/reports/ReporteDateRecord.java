package mx.gob.pjpuebla.trials.statistics.reports;

import java.io.Serializable;

public record ReporteDateRecord(
        String reporte,
        Object extraData
) implements Serializable {
}
