package mx.gob.pjpuebla.trials.core.materias;

import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.Estado;

import java.time.LocalDateTime;

public class MateriaSetUp {

    private MateriaSetUp() {
    }

    public static Materia createMateria() {
        Materia materia = new Materia()
                .setId(1)
                .setNombre("PENAL")
                .setEstado(Estado.ACTIVE)
                .setVersion(0);
        materia.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return materia;
    }
}
