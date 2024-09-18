package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;

import java.time.LocalDateTime;

public class PersonasDocumentosSetUp {
    private PersonasDocumentosSetUp() {
    }

    public static PersonaDocumentoRecord createPersonasDocumentosActor (){
        return new PersonaDocumentoRecord(
                    "Alberto","Gonzales","Martes","Actor",1, 200 );
    }

    public static PersonaDocumento createPersonasDocumentos(Documento documento, TipoPartes tipoPartes) {
        PersonaDocumento personaDocumento = new PersonaDocumento()
                .setId(21)
                .setNombre("Alberto")
                .setApellidoPaterno("Marcos")
                .setApellidoPaterno("Leña")
                .setPseudonimo("leños")
                .setTipoPersona("fisica")
                .setDocumento(documento)
                .setTipoPartes(tipoPartes)
                .setRol(Rol.PRINCIPAL);
                personaDocumento.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return personaDocumento;
    }
}
