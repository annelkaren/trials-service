package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import mx.gob.pjpuebla.trials.core.rubros.RubroRecord;

import java.util.List;

public record InfoExpedienteRecord(
        String expediente,
        String tipoJuicio,
        Integer tipoJuicioId,
        String juezAsignado,
        String fechaPresentacion,
        String asunto,
        String tipoProcedimiento,
        List<RubroRecord> rubros,
        EtapaProcesalRecord etapaProcesal,
        List<ParticipantesRecord> participantes,
        String razonDevolucion,
        String materia,
        Integer materiaId,
        String tipoSistema
) {
}
