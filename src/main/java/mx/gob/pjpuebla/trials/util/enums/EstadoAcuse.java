package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum EstadoAcuse {
    CREADO("Creado"),
    ENTREGADO("Entregado"),
    NOENTREGADO("No entregado"),
    CANCELADO("Cancelado");

    private final String etiqueta;

    EstadoAcuse(String etiqueta){ 
        this.etiqueta = etiqueta;
     }
}
