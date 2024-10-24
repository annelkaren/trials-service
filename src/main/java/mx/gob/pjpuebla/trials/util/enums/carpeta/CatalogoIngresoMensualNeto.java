package mx.gob.pjpuebla.trials.util.enums.carpeta;

import lombok.Getter;

@Getter
public enum CatalogoIngresoMensualNeto {
    UN_SALARIO_MINIMO("1 salario mínimo"),
    DOS_SALARIOS_MINIMOS("2 salarios mínimos"),
    TRES_SALARIOS_MINIMOS("3 salarios mínimos"),
    CUATRO_SALARIOS_MINIMOS("4 salarios mínimos"),
    CINCO_SALARIOS_MINIMOS("5 salarios mínimos"),
    MAS_SALARIOS_MINIMOS("6 salarios mínimos en adelante");

    private final String etiqueta;

    CatalogoIngresoMensualNeto(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}
