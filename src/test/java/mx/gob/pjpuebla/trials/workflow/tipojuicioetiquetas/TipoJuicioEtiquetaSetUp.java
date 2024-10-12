package mx.gob.pjpuebla.trials.workflow.tipojuicioetiquetas;

import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.util.Audit;

import java.time.LocalDateTime;

public class TipoJuicioEtiquetaSetUp {

    private TipoJuicioEtiquetaSetUp() {
    }

    public static TipoJuicioEtiqueta createTipoJuicioEtiqueta(Integer tipoJuicioId) {
        TipoJuicioEtiqueta etiqueta = new TipoJuicioEtiqueta()
                .setId(1)
                .setNombre("documento")
                .setValue("Demanda")
                .setTipoJuicio(new TipoJuicio().setId(tipoJuicioId).setVersion(0));
        etiqueta.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return etiqueta;
    }

}
