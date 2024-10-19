package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

public record PersonaDocumentoItemRecord(
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String pseudonimo,
        String tipoPersona, // fisica o moral
        Integer tipoParte, // actor o demandado
        String curp,
        String ine,
        String domicilio,
        String celular,
        String correoElectronico) {
}
