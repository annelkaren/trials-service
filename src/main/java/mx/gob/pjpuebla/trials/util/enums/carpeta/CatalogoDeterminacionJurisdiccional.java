package mx.gob.pjpuebla.trials.util.enums.carpeta;

import lombok.Getter;
import mx.gob.pjpuebla.trials.workflow.carpeta.CatalogoEnum;

@Getter
public enum CatalogoDeterminacionJurisdiccional implements CatalogoEnum {
    PRESENTACION(0, "Presentación"),
    ADMITIDA(1,"Admitida"),
    ADMITIDA_CON_PREVENCION(2, "Admitida con prevención"),
    DESECHADA(3, "Desechada");

    private final int clave;
    private final String valor;

    CatalogoDeterminacionJurisdiccional(int clave, String valor) {
        this.clave = clave;
        this.valor = valor;
    }
}
