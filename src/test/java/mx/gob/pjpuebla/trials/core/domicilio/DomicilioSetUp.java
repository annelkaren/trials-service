package mx.gob.pjpuebla.trials.core.domicilio;

import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.util.Audit;

import java.time.LocalDateTime;

public class DomicilioSetUp {

    private DomicilioSetUp() {
    }

    public static Domicilio createDomicilio() {
        Domicilio domicilio = new Domicilio()
                .setId(1L)
                .setCalle("Calle Oaxaca")
                .setInterior("209")
                .setExterior("Sin número")
                .setColonia("Las Margaritas")
                .setLocalidad("Miraflores")
                .setCodigoPostal("88630")
                .setMunicipio("Reynosa")
                .setEstadoRepublica("Tamaulipas")
                .setReferencia("Casa naranja");
        domicilio.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return domicilio;
    }

}
