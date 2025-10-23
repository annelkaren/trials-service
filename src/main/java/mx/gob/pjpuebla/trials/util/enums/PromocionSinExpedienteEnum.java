package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum PromocionSinExpedienteEnum {
    REGISTRADO("Registrado"),
    PROMOCION_REGISTRADA("Promoción registrada");

    private final String etiqueta;

    PromocionSinExpedienteEnum(String etiqueta){
        this.etiqueta = etiqueta;
    }
}
