package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.io.Serializable;
import java.util.List;

public record DocPromocionInfoRecord(
        String expediente,
        Integer year,
        String juzgado,
        String actor,
        String demandado,
        String tipoPromocion,
        List<String> anexos
) implements Serializable {
}
