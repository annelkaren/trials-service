package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum EstadoEnvio {
    ENVIADO("Enviado a la OCP"),
    RECIBIDO("Recibido en OCP"),
    ENVIADO_DESTINO("Enviado al destino"),
    RECIBIDO_DESTINO("Recibido en destino");


    private final String etiqueta;

    EstadoEnvio(String etiqueta){
        this.etiqueta = etiqueta;
    }

}
