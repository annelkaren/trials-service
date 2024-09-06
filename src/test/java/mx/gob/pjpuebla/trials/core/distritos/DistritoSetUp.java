package mx.gob.pjpuebla.trials.core.distritos;

import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.time.LocalDateTime;

public class DistritoSetUp {

    private DistritoSetUp() {
    }

    public static Distrito createDistrito() {
        Distrito distrito = new Distrito()
                .setId(1)
                .setNombre("TEZIUTLÁN")
                .setRegion("Oriente")
                .setEstado(Estado.ACTIVE)
                .setVersion(0);
        distrito.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return distrito;
    }

    public static DistritoRecord createDistritoRecord() {
        return new DistritoRecord(1, "TEZIUTLÁN");
    }
}
