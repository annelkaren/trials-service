package mx.gob.pjpuebla.trials.workflow.documentos.records;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;

import java.io.Serializable;
import java.time.LocalDateTime;

public record DocumentoSalidaRecord(
        Integer movid,
        Integer id,
        String folio,
        String expediente,
        Integer juzgadoId,
        String juzgado,
        String materia,
        TipoCarpeta tipoCarpeta,
        TipoDocumento tipoDocumento,
        LocalDateTime fechaRegistro,
        SelloEstatus selloEstatus,
        EstadoCarpeta estatus
) implements Serializable {
}
