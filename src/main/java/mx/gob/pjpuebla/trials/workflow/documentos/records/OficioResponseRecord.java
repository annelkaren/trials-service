package mx.gob.pjpuebla.trials.workflow.documentos.records;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

import java.io.Serializable;
import java.time.LocalDate;

public record OficioResponseRecord(
        Integer docId,
        String folio,
        String dependencia,
        String asunto,
        EstadoCarpeta estatus,
        LocalDate fechaEmision,
        LocalDate fechaEntrega,
        Boolean bandAcuse,
        Boolean bandDigitalizado,
        Character tamanioPapel
) implements Serializable {
}
