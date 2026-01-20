package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.time.LocalDateTime;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;

public record BandejaEntradaRecord(
        Integer movimientoId,
        Integer documentoId,
        String folio,
        String expediente,
        String materia,
        String tipoEntrada,
        String organoJurisdiccional,
        LocalDateTime fechaRegistro,
        SelloEstatus selloEstatus,
        EstadoCarpeta estatus,
        boolean hasFile,
        String motivoDevolucion

) {}
