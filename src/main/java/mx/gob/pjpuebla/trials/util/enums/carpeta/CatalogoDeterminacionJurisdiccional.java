package mx.gob.pjpuebla.trials.util.enums.carpeta;

import lombok.Getter;

@Getter
public enum CatalogoDeterminacionJurisdiccional {
    PRESENTACION("Presentación"),
    ADMITIDA("Admitida"),
    ADMITIDA_CON_PREVENCION("Admitida con prevención"),
    DESECHADA("Desechada");

    private final String etiqueta;

    CatalogoDeterminacionJurisdiccional(String valor) {
        this.etiqueta = valor;
    }
}
