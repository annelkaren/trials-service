package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import lombok.Data;

@Data
public class PersonaDocumentoDTO {

    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String pseudonimo;
    private String tipoPersona; //fisica o moral
    private Integer tipoParte; //actor o demandado
}
