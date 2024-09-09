package mx.gob.pjpuebla.trials.core.bloques;

import java.time.LocalDateTime;
import java.time.LocalTime;

import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.Estado;

public class BloqueSetUp {
    
    private BloqueSetUp(){

    }

    public static Bloque createBloque(){
        Bloque bloque = new Bloque()
                .setId(1)
                .setEstado(Estado.ACTIVE)
                .setHoraInicial(LocalTime.now())
                .setHoraFinal(LocalTime.now());
            bloque.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7",
                "6b13785f-d213-4585-a76b-437ffe57c9c7"));

        return bloque;
    }

}
