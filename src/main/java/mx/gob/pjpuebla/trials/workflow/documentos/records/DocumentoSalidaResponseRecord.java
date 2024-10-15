package mx.gob.pjpuebla.trials.workflow.documentos.records;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public record DocumentoSalidaResponseRecord(
        Integer movid,
        Integer id,
        String folio,
        String expediente,
        Integer juzgadoId,
        String juzgado,
        String materia,
        String tipoEntrada,
        LocalDateTime fechaRegistro,
        SelloEstatus selloEstatus,
        EstadoCarpeta estatus
) implements Serializable {
}
