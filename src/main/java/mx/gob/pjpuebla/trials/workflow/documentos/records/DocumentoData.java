package mx.gob.pjpuebla.trials.workflow.documentos.records;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DocumentoData implements Serializable {

    private List<String> tipoJuicios;

}

