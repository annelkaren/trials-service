package mx.gob.pjpuebla.trials.workflow.documentos.records;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DocumentoData implements Serializable {

    private List<String> tiposJuicios;
    private Integer tieneAbogado;
    private String nombreAbogado;
    private String cedulaAbogado;
    private String correoAbogado;
    private String domicilio;
}

