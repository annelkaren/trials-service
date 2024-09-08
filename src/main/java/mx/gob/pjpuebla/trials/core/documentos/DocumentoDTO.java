package mx.gob.pjpuebla.trials.core.documentos;

import lombok.Data;
import mx.gob.pjpuebla.trials.core.personasdocumentos.PersonaDocumentoDTO;

import java.util.List;

@Data
public class DocumentoDTO {

    List<PersonaDocumentoDTO> personaDocumento;
    List<String> anexos;
    Integer tipoJuicioId;
}
