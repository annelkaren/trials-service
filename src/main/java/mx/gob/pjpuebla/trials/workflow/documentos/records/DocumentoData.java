package mx.gob.pjpuebla.trials.workflow.documentos.records;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DocumentoData implements Serializable {

    private List<String> tipoJuicios;
    private Integer tieneAbogado;
    private String nombreAgobago;
    private String cedulaAgogado;
    private String correoAbogado;
}

