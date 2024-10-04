package mx.gob.pjpuebla.trials.core.eventos;

import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventoSetUp {

    private EventoSetUp(){

    }

    public static Evento createEvento(){
        Evento evento = new Evento()
                .setId(1)
                .setVersion(0)
                .setDescripcion("DIA INHABIL")
                .setDiaInicio(LocalDate.parse("2024-11-01"))
                .setDiaFin(LocalDate.parse("2024-11-01"))
                .setEstado(Estado.ACTIVE);

        evento.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7",
                "6b13785f-d213-4585-a76b-437ffe57c9c7"));

        return evento;
    }
}
