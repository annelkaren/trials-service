package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import java.io.Serializable;

public record PersonaDocumentoNameRecord(
        Integer id,
        String nombreCompleto
) implements Serializable {
}
