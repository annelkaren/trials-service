package mx.gob.pjpuebla.trials.util.enums.carpeta;

import lombok.Getter;
import mx.gob.pjpuebla.trials.workflow.carpeta.CatalogoEnum;

@Getter
public enum CatalogoIngresoMensualNeto implements CatalogoEnum {
    UNO_SALARIO_MINIMO(0,"1 salario mínimo"),
    DOS_SALARIOS_MINIMOS(1, "2 salarios mínimos"),
    TRES_SALARIOS_MINIMOS(2, "3 salarios mínimos"),
    CUATRO_SALARIOS_MINIMOS(3, "4 salarios mínimos"),
    CINCO_SALARIOS_MINIMOS(4, "5 salarios mínimos"),
    MAS_SALARIOS_MINIMOS(4, "6 salarios mínimos en adelante");

    private final int clave;
    private final String valor;

    CatalogoIngresoMensualNeto(int clave, String valor) {
        this.clave = clave;
        this.valor = valor;
    }
}
