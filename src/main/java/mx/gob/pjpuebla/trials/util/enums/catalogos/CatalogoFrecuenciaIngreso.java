package mx.gob.pjpuebla.trials.util.enums.catalogos;

import lombok.Getter;
import mx.gob.pjpuebla.trials.workflow.carpeta.CatalogoEnum;

@Getter
public enum CatalogoFrecuenciaIngreso implements CatalogoEnum {
    MENSUAL(0, "Mensual"),
    QUINCENAL(1, "Quincenal"),
    SEMANAL(2, "Semanal"),
    DIARIO(3, "Diario");

    private final int clave;
    private final String valor;

    CatalogoFrecuenciaIngreso(int clave, String valor) {
        this.clave = clave;
        this.valor = valor;
    }
}
