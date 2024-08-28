package mx.gob.pjpuebla.trials.core.estadocivil;

import mx.gob.pjpuebla.trials.util.Audit;
import java.time.LocalDateTime;
public class EstadoCivilSetUp {

    private EstadoCivilSetUp(){
    }

    public static EstadoCivil createEstadoCivil(){
        EstadoCivil estadoCivil = new EstadoCivil()
                .setId(1)
                .setNombre("Soltero/a")
                .setEstado("A")
                .setVersion(1);
                estadoCivil.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return estadoCivil;
    }

    public static EstadoCivilRecord createEstadoCivilRecord(){
        return new EstadoCivilRecord(1, "Soltero/a");
    }
}
