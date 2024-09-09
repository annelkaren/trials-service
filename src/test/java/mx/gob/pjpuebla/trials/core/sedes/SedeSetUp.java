package mx.gob.pjpuebla.trials.core.sedes;

import mx.gob.pjpuebla.trials.core.distritos.DistritoRecord;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.Tipo;

import java.time.LocalDateTime;

public class SedeSetUp {

    private SedeSetUp() {
    }

    public static Sede createSede() {
        Sede sede = new Sede()
                .setId(1)
                .setVersion(0)
                .setNombre("Sede")
                .setTipo(Tipo.EXTERNO)
                .setEstado(Estado.ACTIVE);
        sede.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return sede;
    }

    public static Sede createSede(Estado estado) {
        Sede sede = new Sede()
                .setId(1)
                .setVersion(0)
                .setNombre("Sede")
                .setTipo(Tipo.EXTERNO)
                .setEstado(estado);
        sede.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return sede;
    }

    public static SedeRecordResponse sedeRecordResponse() {
        return new SedeRecordResponse(1, "Sede", Estado.ACTIVE);
    }

    public static SedeRecord sedeRecord() {
        return new SedeRecord(1, 0, "Sede",
                Estado.ACTIVE,
                Tipo.EXTERNO,
                "", "",
                new DistritoRecord(1, ""),
                new DomicilioRecord(1L, "", "", "", "", "", "", "", "", ""));
    }
}
