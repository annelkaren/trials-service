package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum EstadoMigracion {
    EXPEDIENTE_MIGRADO("Expediente migrado"),
    DOCUMENTOS_MIGRADOS("Documentos del expediente migrados"),
    MIGRADO_COMPLETADO("Migración completada"),
    EXPEDIENTE_TURNADO("Expediente turnado"),
    NO_MIGRADO("Expediente no migrado");

    private final String etiqueta;

    EstadoMigracion(String etiqueta){
        this.etiqueta = etiqueta;
    }
}
