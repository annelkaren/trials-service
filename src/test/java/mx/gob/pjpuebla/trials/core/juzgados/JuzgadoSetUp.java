package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRecord;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.time.LocalDateTime;
import java.util.Arrays;

public class JuzgadoSetUp {

    private JuzgadoSetUp() {
    }

    public static Juzgado createJuzgado(Estado estado) {
        Juzgado juzgado = new Juzgado()
                .setId(1)
                .setVersion(0)
                .setNombre("Juzgado")
                .setEstado(estado);
        juzgado.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return juzgado;
    }

    public static Juzgado createJuzgado() {
        Juzgado juzgado = new Juzgado()
                .setId(1)
                .setVersion(0)
                .setNombre("Juzgado")
                .setEstado(Estado.ACTIVE)
                .setMateria(MateriaSetUp.createMateria())
                .setSede(SedeSetUp.createSede());
        juzgado.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return juzgado;
    }

    public static Juzgado createJuzgado(Materia materia, Sede sede) {
        Juzgado juzgado = new Juzgado()
                .setId(1)
                .setVersion(0)
                .setNombre("Juzgado")
                .setEstado(Estado.ACTIVE)
                .setMateria(materia)
                .setSede(sede)
                .setTipoJuicios(Arrays.asList(
                        new TipoJuicio().setId(1).setNombre("Tipo Juicio Uno"),
                        new TipoJuicio().setId(2).setNombre("Tipo Juicio Dos")
                ));
        juzgado.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return juzgado;
    }

    public static JuzgadoRecord createJuzgadoRecord(Juzgado juzgado, Integer materiaId, Integer sedeId) {
        return new JuzgadoRecord(
                juzgado.getId(),
                juzgado.getVersion(),
                juzgado.getNombre(),
                Estado.ACTIVE,
                materiaId,
                sedeId,
                0,
                0,
                Arrays.asList(
                        new TipoJuicioRecord(1, "Tipo Juicio Uno", null, null),
                        new TipoJuicioRecord(2, "Tipo Juicio Dos", null, null)
                )
        );
    }


    public static JuzgadoRecordItem createJuzgadoRecordResponse(Juzgado juzgado, String materia) {
        return new JuzgadoRecordItem(
                juzgado.getId(),
                juzgado.getNombre(),
                Estado.ACTIVE,
                materia
        );
    }

}
