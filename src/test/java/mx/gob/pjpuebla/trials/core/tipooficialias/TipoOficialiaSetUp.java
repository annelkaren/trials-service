package mx.gob.pjpuebla.trials.core.tipooficialias;

import mx.gob.pjpuebla.trials.util.Audit;

import java.time.LocalDateTime;

public class TipoOficialiaSetUp {

    private TipoOficialiaSetUp(){

    }

    public static TipoOficialias CreatetipoOficialia(){
        TipoOficialias tipoOficialias = new TipoOficialias()
                .setId(1)
                .setNombre("Común")
                .setEstado("A")
                .setVersion(1);
        tipoOficialias.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return tipoOficialias;
    }
}
