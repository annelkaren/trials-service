package mx.gob.pjpuebla.trials.workflow.carpeta.records;

public record ApelacionPersonaRecord(
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String pseudonimo,
        String tipoPersona,
        Integer tipoPartes
) {
}
