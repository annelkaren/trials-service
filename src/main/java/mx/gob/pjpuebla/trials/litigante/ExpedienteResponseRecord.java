package mx.gob.pjpuebla.trials.litigante;

import java.util.List;

public record ExpedienteResponseRecord(
    String numeroExpediente,
    String materia,
    String tipoJuicio,
    String juzgado,
    Long notificacionesPendientesPorCompletar,
    List<DocumentoResponseRecord> documentosExpediente
) {} 