package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.time.LocalDateTime;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

public record BandejaHistorialRecord(
      Integer movimientoId,
        String folio,
        String expediente,
        String materia,
        String tipoEntrada,
        LocalDateTime fechaRegistro,
        EstadoCarpeta estatus,
        String organoJurisdiccional,
        String estaEnJuzgado
) {}
