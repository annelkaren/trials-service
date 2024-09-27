package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.Rol;

import java.time.LocalDateTime;

public class PersonasDocumentosSetUp {
    private PersonasDocumentosSetUp() {
    }

    public static PersonaDocumentoRecord createPersonasDocumento() {
        return new PersonaDocumentoRecord(
                "Alberto", "Gonzalez", "", null, "fisica", "Actor", 1, 200);
    }

    public static PersonaDocumentoItemRecord createPersonaDocumentoItemRecord() {
        return new PersonaDocumentoItemRecord(
                "Alberto", "Gonzalez", null, "Actor", "fisica", 1);
    }

    public static PersonaDocumento createPersonasDocumentos() {
        PersonaDocumento personaDocumento = new PersonaDocumento()
                .setId(21)
                .setNombre("Alberto")
                .setApellidoPaterno("Marcos")
                .setApellidoMaterno("Leña")
                .setPseudonimo("leños")
                .setTipoPersona("fisica")
                .setRol(Rol.PRINCIPAL);
        personaDocumento.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return personaDocumento;
    }
}
