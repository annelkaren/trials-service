package mx.gob.pjpuebla.trials.workflow.documentos.records;


import java.io.Serializable;

public record DocumentoAnexoRecord (

        Integer documentoId,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String pseudonimo,
        String tipoPersona,
        String tipoParteNombre,
        Integer tipoParteId

) implements Serializable {
}