package mx.gob.pjpuebla.trials.core.tiposistema;

import mx.gob.pjpuebla.trials.util.Audit;

import java.time.LocalDateTime;

public class TipoSistemaSetUp {

    private TipoSistemaSetUp() {
    }

    public static TipoSistema createTipoSistema() {
        TipoSistema tipoSistema = new TipoSistema()
                .setId(1)
                .setNombre("Tradicional")
                .setEstado("A")
                .setVersion(0);
        tipoSistema.setAudit(
                new Audit(
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        "6b13785f-d213-4585-a76b-437ffe57c9c7",
                        "6b13785f-d213-4585-a76b-437ffe57c9c7")
        );
        return tipoSistema;
    }


}
