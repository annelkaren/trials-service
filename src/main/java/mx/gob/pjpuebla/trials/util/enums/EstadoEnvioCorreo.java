package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum EstadoEnvioCorreo {
    PENDIENTE("Pendiente"),
    ENVIADO("Enviado"),
    RECIBIDO("Recibido"),
    LEIDO("Leido");

    private final String etiqueta;

    EstadoEnvioCorreo(String etiqueta){
        this.etiqueta = etiqueta;
    }
    
}
