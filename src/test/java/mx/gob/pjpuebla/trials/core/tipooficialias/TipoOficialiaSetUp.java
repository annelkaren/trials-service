package mx.gob.pjpuebla.trials.core.tipooficialias;


import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.Estado;

import java.time.LocalDateTime;

public class TipoOficialiaSetUp {

    private TipoOficialiaSetUp(){

    }

    public static TipoOficialia createtipoOficialia(){
        TipoOficialia tipoOficialia = new TipoOficialia()
                .setId(1)
                .setNombre("Común")
                .setEstado(Estado.ACTIVE)
                .setVersion(1);
        tipoOficialia.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return tipoOficialia;
    }

    public static TipoOficialiaRecord createTipoOficialiaRecord(){
        return new TipoOficialiaRecord(1, "Mayor");
    }
}
