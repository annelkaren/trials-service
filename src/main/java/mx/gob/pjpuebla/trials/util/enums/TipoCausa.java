package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum TipoCausa {
    CONTROL_JUDICIAL_PREVIO("Control judicial previo"),
    CONTROL_ACTOS_INVESTIGACION("Control actos de investigación"),
    EXHORTO("Exhorto"),
    JUICIO_ORAL("Juicio Oral"),
    EJECUCION("Ejecución");

    private final String tipoCausa;

    TipoCausa(String tipoCausa) {
        this.tipoCausa = tipoCausa;
    }
}