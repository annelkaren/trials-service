package mx.gob.pjpuebla.trials.core.documentos;

import lombok.Data;
import mx.gob.pjpuebla.trials.core.personasdocumentos.PersonaDocumentoDTO;

import java.util.List;

@Data
public class DocumentoDTO {

    PersonaDocumentoDTO actor;
    PersonaDocumentoDTO demandado;
    List<String> anexos;
    Integer tipoJuicioId;
}
