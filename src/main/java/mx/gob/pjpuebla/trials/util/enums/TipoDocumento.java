package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum TipoDocumento {
    PROMOCION("PROMOCIÓN"), 
    OFICIO("OFICIO"),
    ACUERDO("ACUERDO"),
    AMPARO("AMPARO"),
    EXHORTO_SALIDA("EXHORTO DE SALIDA"),
    SENTENCIA("SENTENCIA"),
    SENTENCIA_PUBLICA("SENTENCIA_PUBLICA"),
    DOCUMENTO_IDENTIFICACION("DOCUMENTO_IDENTIFICACIÓN");

    private final String etiqueta;

    TipoDocumento(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}
