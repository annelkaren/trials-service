package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum EstadoEnvioNotificacionesSalas {
    PENDIENTE("Pendiente"),
    ENVIADO("Enviado"),
    RECIBIDO("Recibido"),
    LEIDO("Leido");

    private final String etiqueta;

    EstadoEnvioNotificacionesSalas(String etiqueta){
        this.etiqueta = etiqueta;
    }
    
}
