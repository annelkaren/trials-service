package mx.gob.pjpuebla.trials.core.etapaprocesal.record;

public record EtapaProcesalRecord(
        Integer id,
        String etapaPrecesal,
        Integer idProcedimiento,
        String  procedimiento,
        Integer idtipoSistema,
        Integer idmateria
) {
}
