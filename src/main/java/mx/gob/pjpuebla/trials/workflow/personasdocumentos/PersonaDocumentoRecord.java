package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRecord;

import java.util.List;

public record PersonaDocumentoRecord(
        Integer id,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno
) {

    public PersonaDocumentoRecord withRoles(List<PersonaDocumento> documento){
        return new PersonaDocumentoRecord(
                id(), nombre(), apellidoPaterno(), apellidoMaterno()
        );
    }
}
