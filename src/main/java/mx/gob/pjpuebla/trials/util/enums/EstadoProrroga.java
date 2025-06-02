package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum EstadoProrroga {
    SOLICITADA("Solicitada"),
    AUTORIZADA("Autorizada"),
    RECHAZADA("Rechazada");

    private final String etiqueta;

    EstadoProrroga(String etiqueta) {
        this.etiqueta = etiqueta;
    }   

}
