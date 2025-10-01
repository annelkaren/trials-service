package mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada;

import java.time.LocalDateTime;

public record BandejaEntradaFilter(
    String folio,
    String expediente,
    String materia,
    String tipoEntrada,
    String organoJurisdiccional,
    LocalDateTime fechaRegistro,
    String key
) {}
