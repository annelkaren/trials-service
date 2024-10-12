package mx.gob.pjpuebla.trials.workflow.audiencias.record;

public record ExtraAudienciaSelloRecord(
        String nombreJuez,
        String nombreSala,
        String nombreTipoJuicio,
        String fechaAudiencia,
        String domicilio
) {
}
