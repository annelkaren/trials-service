package mx.gob.pjpuebla.trials.core.especialidadJuzgado;

import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.Estado;

import java.time.LocalDateTime;

public class EspecialidadesSetUp {

    private EspecialidadesSetUp() {
    }

    public static Especialidades createEspecialidades() {
        Especialidades especialidades = new Especialidades()
                .setId(1)
                .setNombre("Juzgado Especializado en Juicios")
                .setEstado(Estado.ACTIVE)
                .setVersion(0);
        especialidades.setAudit(new Audit(LocalDateTime.now(),
                                            LocalDateTime.now(),
                                            "6b13785f-d213-4585-a76b-437ffe57c9c7",
                                            "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return especialidades;
    }

    public static EspecialidadesRecord createEspecialidadesRecord(){
        return new EspecialidadesRecord(1, "Juzgado Especializado en Juicios");
    }

}

