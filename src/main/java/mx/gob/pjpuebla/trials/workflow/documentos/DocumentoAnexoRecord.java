package mx.gob.pjpuebla.trials.workflow.documentos;


import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class DocumentoAnexoRecord {

    private Integer documentoId;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String pseudonimo;
    private String tipoPersona;
    private String tipoParteNombre;
    private Integer tipoParteId;

}