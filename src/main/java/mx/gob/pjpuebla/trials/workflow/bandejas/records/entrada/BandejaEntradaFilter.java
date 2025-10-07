package mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada;

import java.time.LocalDate;


public record BandejaEntradaFilter(
    String folio,
    String expediente,
    String materia,
    String tipoEntrada,
    String organoJurisdiccional,
    LocalDate fechaRegistro,
    String key
) {}
