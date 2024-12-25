package mx.gob.pjpuebla.trials.core.materiapericial;

public class MateriaPericialSetUp {
    private MateriaPericialSetUp() {
    }

    public static MateriaPericial createMateriaPericial() {
        return new MateriaPericial().setId(1).setNombre("Documentoscopía y Grafoscopía");
    }
}
