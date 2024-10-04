package mx.gob.pjpuebla.trials.workflow.personasdocumentos;


import java.io.Serializable;

public record PersonaDocumentoRecord (

        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String pseudonimo,
        String tipoPersona,
        String curp,
        String domicilio,
        String celular,
        String correoElectronico,
        String tipoParte,
        Integer tipoParteId,
        Integer carpetaId

) implements Serializable {
}