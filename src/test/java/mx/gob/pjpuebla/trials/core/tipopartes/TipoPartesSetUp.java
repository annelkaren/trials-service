package mx.gob.pjpuebla.trials.core.tipopartes;

import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.Estado;

import java.time.LocalDateTime;

public class TipoPartesSetUp {

    private TipoPartesSetUp() {
    }

    public static TipoPartes createTipoPartes() {
        TipoPartes tipoPartes = new TipoPartes()
                .setId(1)
                .setNombre("Actor")
                .setEstado(Estado.ACTIVE)
                .setVersion(0);
        tipoPartes.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return tipoPartes;
    }

    public static TipoPartesRecord createTipoPartesRecord() {
        return new TipoPartesRecord(1, "Actor", "Laboral (Tradicional)");
    }

}
