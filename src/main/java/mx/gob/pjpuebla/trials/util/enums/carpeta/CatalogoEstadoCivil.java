package mx.gob.pjpuebla.trials.util.enums.carpeta;

import lombok.Getter;

@Getter
public enum CatalogoEstadoCivil {
    UNION_LIBRE("Unión libre"),
    DIVORCIADO("Divorciado/a"),
    SEPARADO("Separado/a"),
    SOLTERO("Soltero/a"),
    CONCUBINATO("Concubinato"),
    VIUDO("Viudo/a"),
    CASADO("Casado/a"),
    SOCIEDAD_CONVICENCIA("Sociedad de convivencia"),
    SEPARADO_EN_PROCESO_JUDICIAL("Separado/a en Proceso Judicial"),
    NO_IDENTIFICADO("No identificado");

    private final String etiqueta;

    CatalogoEstadoCivil(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}
