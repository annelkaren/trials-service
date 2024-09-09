package mx.gob.pjpuebla.trials.core.salas;

import java.time.LocalDateTime;
import java.time.LocalTime;

import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRecord;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecordResponse;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRecord;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.Estado;

public class SalaSetUp {

    private SalaSetUp() {

    }

    public static Sala createSala() {
        Sala sala = new Sala()
                .setId(1)
                .setVersion(0)
                .setEstado(Estado.ACTIVE)
                .setNombre("1");
        sala.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7",
                "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return sala;
    }

    public static Sala createSala(Estado estado) {
        Sala sala = new Sala()
                .setId(1)
                .setVersion(0)
                .setEstado(estado)
                .setNombre("1");
        sala.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7",
                "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return sala;
    }
    
    public static SalaRecordResponse salaRecordResponse() {
        Long idPersona = (long) 1;
        return new SalaRecordResponse(
                1,
                "1",
                Estado.ACTIVE,
                0,
                new PersonaRecord(idPersona, "Angel", "Lopez", "Perez", "AngLoPer"),
                new JuzgadoRecordResponse(1, "juzgado 1", Estado.ACTIVE, "Civil"),
                new BloqueRecord(1, LocalTime.now(), LocalTime.now()));
    }


    public static SalaRecord salaRecord() {
        return new SalaRecord(
            1, 
            "1",
            "Juez Juan Perez", 
            "Juzgado Primero", 
            "09:00 - 17:00", 
            Estado.ACTIVE 
        );
    }

}