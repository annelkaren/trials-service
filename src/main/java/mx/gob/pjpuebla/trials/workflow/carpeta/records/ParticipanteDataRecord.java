package mx.gob.pjpuebla.trials.workflow.carpeta.records;


import mx.gob.pjpuebla.trials.util.enums.Rol;

public record ParticipanteDataRecord(
        Integer id,
        String nombre,
        Rol rol
) {
}
