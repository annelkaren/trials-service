package mx.gob.pjpuebla.trials.core.tiposistema;


import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.Estado;

import java.time.LocalDateTime;

public class TipoSistemaSetUp {

    private TipoSistemaSetUp(){

    }
    public static TipoSistema createTipoSistema(){
        TipoSistema tipoSistema = new TipoSistema()
                .setId(1)
                .setNombre("Tradicional")
                .setEstado(Estado.ACTIVE) // setEstado("A")
                .setVersion(1);
        tipoSistema.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return tipoSistema;
    }

    public static TipoSistemaRecord createTipoSistemaRecord(){
        return new TipoSistemaRecord(1, "Tradicional");
    }
}
