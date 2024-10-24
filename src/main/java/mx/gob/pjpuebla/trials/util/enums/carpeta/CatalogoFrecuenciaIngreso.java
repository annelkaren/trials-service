package mx.gob.pjpuebla.trials.util.enums.carpeta;

import lombok.Getter;

@Getter
public enum CatalogoFrecuenciaIngreso {
    MENSUAL("Mensual"),
    QUINCENAL("Quincenal"),
    SEMANAL("Semanal"),
    DIARIO("Diario");

    private final String etiqueta;

    CatalogoFrecuenciaIngreso(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}
