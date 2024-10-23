package mx.gob.pjpuebla.trials.util.enums.catalogos;

import lombok.Getter;
import mx.gob.pjpuebla.trials.workflow.carpeta.CatalogoEnum;

@Getter
public enum CatalogoTiposDomicilio implements CatalogoEnum {
    PARTICULAR(0, "Particular"),
    PROCESAL(1, "Procesal"),
    TRABAJO(2, "Trabajo");

    private final int clave;
    private final String valor;

    CatalogoTiposDomicilio(int clave, String valor) {
        this.clave = clave;
        this.valor = valor;
    }
}
