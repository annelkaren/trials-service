package mx.gob.pjpuebla.trials.core.personasdocumentos;

import lombok.Data;

@Data
public class PersonaDocumentoDTO {

    private String nombre;
    private String apelidoPaterno;
    private String apellidoMaterno;
    private String pseudonimo;
    private String tipoPersona; //fisica o moral
    private Integer tipoparte; //actor o demandado
}
