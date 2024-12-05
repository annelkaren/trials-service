package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum TipoDocumento {
    PROMOCION("PROMOCIÓN"),
    OFICIO("OFICIO"),
    ACUERDO("ACUERDO"),
    AMPARO("AMPARO"),
    EXHORTO_SALIDA("EXHORTO DE SALIDA"),
    SENTENCIA("SENTENCIA");

    private final String etiqueta;

    TipoDocumento(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}
