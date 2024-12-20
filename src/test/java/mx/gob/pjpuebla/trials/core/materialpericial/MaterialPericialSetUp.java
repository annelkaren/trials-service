package mx.gob.pjpuebla.trials.core.materialpericial;

public class MaterialPericialSetUp {
    private MaterialPericialSetUp() {
    }

    public static MaterialPericial createMaterialPericial() {
        return new MaterialPericial().setId(1).setNombre("Documentoscopía y Grafoscopía");
    }
}
