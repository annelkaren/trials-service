package mx.gob.pjpuebla.trials.workflow.personasdocumentos;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PersonaDocumentoRecord {
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String tipoParteNombre;
    private Integer tipoParteId;
    private Integer documentoId;

}
