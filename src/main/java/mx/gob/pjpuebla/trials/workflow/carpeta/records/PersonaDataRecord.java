package mx.gob.pjpuebla.trials.workflow.carpeta.records;

public record PersonaDataRecord(
        Integer id,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String tipoPartesNombre
) {
}
