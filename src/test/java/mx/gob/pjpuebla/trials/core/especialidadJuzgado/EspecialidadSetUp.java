package mx.gob.pjpuebla.trials.core.especialidadJuzgado;

import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.Estado;

import java.time.LocalDateTime;

public class EspecialidadSetUp {

    private EspecialidadSetUp() {
    }

    public static Especialidad createEspecialidad() {
        Especialidad especialidad = new Especialidad()
                .setId(1)
                .setNombre("Juzgado Especializado en Juicios")
                .setEstado(Estado.ACTIVE)
                .setVersion(0);
        especialidad.setAudit(new Audit(LocalDateTime.now(),
                                            LocalDateTime.now(),
                                            "6b13785f-d213-4585-a76b-437ffe57c9c7",
                                            "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return especialidad;
    }

    public static EspecialidadRecord createEspecialidadRecord(){
        return new EspecialidadRecord(1, "Juzgado Especializado en Juicios");
    }

}

