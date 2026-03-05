package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum EstadoEnvioCorreo {
    PENDIENTE_ENVIO("Pendiente de envio"),
    ENVIADO("Enviado"),
    RECIBIDO("Recibido"),
    LEIDO("Leido"),
    NO_LEIDO("No leido"),
    NO_ENVIADO("No enviado"),
    NO_ENTREGADO("No entregado"),
    ERROR("Error");

    private final String etiqueta;

    EstadoEnvioCorreo(String etiqueta){
        this.etiqueta = etiqueta;
    }
    
}
