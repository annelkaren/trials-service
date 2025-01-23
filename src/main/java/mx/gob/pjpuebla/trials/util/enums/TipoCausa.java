package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum TipoCausa {
    CARPETAS_JUDICIALES("Carpetas judiciales", "CJ"),
    CONTROL_JUDICIAL_PREVIO("Control judicial previo", "CJP"),
    CONTROL_ACTOS_INVESTIGACION("Control actos de investigación", "CAI"),
    EXHORTO("Exhorto", "EXT"),
    REQUISITORIAS("Requisitorias", "REQ"),
    JUICIO_ORAL("Juicio Oral", "JO"),
    EJECUCION("Ejecución", "EJE"),
    PROVIDENCIA_PRECAUTORIA("Providencia precautoria", "PP"),
    MEDIDAS_PROTECCION("Medidas de protección", "MP");

    private final String tipoCausa;
    private final String iniciales;

    TipoCausa(String tipoCausa, String iniciales) {
        this.tipoCausa = tipoCausa;
        this.iniciales = iniciales;
    }
}