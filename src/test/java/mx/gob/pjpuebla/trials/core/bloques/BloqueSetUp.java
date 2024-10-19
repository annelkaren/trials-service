package mx.gob.pjpuebla.trials.core.bloques;

import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class BloqueSetUp {

    private BloqueSetUp() {

    }

    public static Bloque createBloque() {
        Bloque bloque = new Bloque()
                .setId(1)
                .setEstado(Estado.ACTIVE)
                .setHoraInicial(LocalTime.now())
                .setHoraFinal(LocalTime.now());
        bloque.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7",
                "6b13785f-d213-4585-a76b-437ffe57c9c7"));

        return bloque;
    }

    public static BloqueRecordResponse createBloqueRecordResponse() {
        return new BloqueRecordResponse(1, LocalTime.of(8, 30), LocalTime.of(9, 30),  Estado.ACTIVE);
    }

    public static BloqueRecord createBloqueRecord(){
        return new BloqueRecord(1, LocalTime.of(8, 30), LocalTime.of(9, 30));
                                      
    }

}
