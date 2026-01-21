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
        String organoJurisdiccional, //juzgado, se cambia nombre para mentener semantica con el front end.
        String materia,
        String tipoEntrada,
        LocalDateTime fechaRegistro,
        SelloEstatus selloEstatus,
        EstadoCarpeta estatus
) implements Serializable {
}
