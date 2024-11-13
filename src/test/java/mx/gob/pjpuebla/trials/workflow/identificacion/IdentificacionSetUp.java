package mx.gob.pjpuebla.trials.workflow.identificacion;

import mx.gob.pjpuebla.trials.util.Audit;

import java.time.LocalDateTime;
import java.util.List;

public class IdentificacionSetUp {

    private IdentificacionSetUp(){
    }

    public static List<Identificacion> createIdentificaciones() {
        Identificacion identificacion1 = new Identificacion()
                .setId(1)
                .setName("Gafete Institucional defensoría pública")
                .setVersion(0);
        identificacion1.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(),
                "6b13785f-d213-4585-a76b-437ffe57c9c7",
                "6b13785f-d213-4585-a76b-437ffe57c9c7"));

        Identificacion identificacion2 = new Identificacion()
                .setId(2)
                .setName("Gafete Institucional centro de meditación")
                .setVersion(0);
        identificacion2.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(),
                "7a23456f-d213-4585-a76b-437ffe57c9c8",
                "7a23456f-d213-4585-a76b-437ffe57c9c8"));

        return List.of(identificacion1, identificacion2);
    }

    public static IdentificacionDocRecord createIdentificacionDoc() {
        return new IdentificacionDocRecord(
                1,
                "Gafete Institucional defensoría pública"
        );
    }
}
