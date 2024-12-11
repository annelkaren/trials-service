package mx.gob.pjpuebla.trials.workflow.documentos.records;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;

import java.time.LocalDateTime;

public record DocumentoGridRecord(
        Integer id,
        String folio,
        String expediente,
        String materia,
        String tipoEntrada,
        LocalDateTime fechaRegistro,
        SelloEstatus selloEstatus,
        EstadoCarpeta estatus,
        boolean hasFile,
        String organoJurisdiccional
        ) {
}
