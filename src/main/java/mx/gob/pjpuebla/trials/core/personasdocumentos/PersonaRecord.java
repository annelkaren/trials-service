package mx.gob.pjpuebla.trials.core.personasdocumentos;

import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRecord;
//Persona
public record PersonaRecord(
        Integer id,
        String nombre,
        String apelidoPaterno,
        String apellidoMaterno,
        String pseudonimo,
//      fisica o moral
        String tipoPersona,
//        actor o demandado
        TipoPartesRecord tipoparte

) {
}