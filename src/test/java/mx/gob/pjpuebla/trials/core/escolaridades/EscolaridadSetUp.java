package mx.gob.pjpuebla.trials.core.escolaridades;

import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.time.LocalDateTime;

public class EscolaridadSetUp {

    private EscolaridadSetUp() {
    }

    public static Escolaridad createEscolaridad() {
        Escolaridad escolaridad = new Escolaridad()
                .setId(1)
                .setEstado(Estado.ACTIVE)
                .setNombre("Doctorado")
                .setNivel("Superior");
        escolaridad.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return escolaridad;
    }

}
