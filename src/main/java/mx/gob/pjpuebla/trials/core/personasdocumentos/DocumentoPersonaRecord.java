package mx.gob.pjpuebla.trials.core.personasdocumentos;

import mx.gob.pjpuebla.trials.core.anexos.Anexo;
import mx.gob.pjpuebla.trials.core.anexos.AnexoRecord;

import java.util.List;

//principal
public record DocumentoPersonaRecord(
        List<PersonaRecord> persona,
        Integer tipoJuicio,
        List<AnexoRecord> anexo
//        List<Anexo> anexo

) {
}
