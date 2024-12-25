package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;

import java.io.Serializable;
import java.util.List;

public record ApelacionRecord(
        Integer carpetaId,
        List<ApelacionPersonaRecord> apelacionPersonaRecords,
        List<Anexo> anexos,
        String otroNombreActor,
        String otroNombreDemandado
) implements Serializable {
}
