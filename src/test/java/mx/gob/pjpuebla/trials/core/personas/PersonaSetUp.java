package mx.gob.pjpuebla.trials.core.personas;

import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.Estado;

import java.time.LocalDateTime;

public class PersonaSetUp {

    private PersonaSetUp() {
    }

    public static Persona createPersona() {
        Persona persona = new Persona()
                .setId(1L)
                .setVersion(0)
                .setNombre("Juan")
                .setApellidoPaterno("Perez")
                .setApellidoMaterno("Gonzalitos")
                .setEstado(Estado.ACTIVE)
                .setCorreoElectronico("random@random.com");
        persona.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return persona;
    }

    public static PersonaRecord createPersonaRecord() {
        return new PersonaRecord(1L, "Juan", "Perez", "Gonzalitos","El ratón Pérez");
    }
}
