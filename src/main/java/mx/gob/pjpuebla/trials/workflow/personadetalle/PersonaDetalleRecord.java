package mx.gob.pjpuebla.trials.workflow.personadetalle;

import java.io.Serializable;

public record PersonaDetalleRecord (
    Integer id,
    Integer personaDocumentoId,
    Long domicilioId
) implements Serializable {}
