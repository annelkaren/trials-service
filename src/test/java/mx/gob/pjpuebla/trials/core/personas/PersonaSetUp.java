package mx.gob.pjpuebla.trials.core.personas;

import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.Sexo;

import java.time.LocalDate;
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
                .setEstado(Estado.ACTIVE)
                .setOcupacion("Maestro")
                .setCurp("JEBR102105MPUEELO9")
                .setSexo(Sexo.FEMENINO)
                .setFechaNacimiento(LocalDate.of(1992,10,24))
                .setCorreoElectronico("random@random.com")
                .setJuzgado(JuzgadoSetUp.createJuzgado());
        persona.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return persona;
    }

    public static PersonaRecord createPersonaRecord() {
        return new PersonaRecord(1L, 0, "Juan", "Perez",
                "Gonzalitos", "XXXX111111XXXXXX11", "", LocalDate.of(1992, 1, 1),
                "juanperez@mail.com", "", "", Sexo.FEMENINO, "", Estado.ACTIVE,
                1, 1, 1, null,null, "", null);
    }

    public static PersonaRecordResponse createPersonaRecordResponse() {
        return new PersonaRecordResponse(1L, "Juan Perez", "jp@mail.com", "111-111-1111", "Juzgado", "");
    }
}
