package mx.gob.pjpuebla.migracion.usecases;

import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

public record MigracionExpedienteResult(Carpeta carpeta, Integer migracionId) {}
