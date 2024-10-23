package mx.gob.pjpuebla.trials.util.enums.carpeta;

import lombok.Getter;
import mx.gob.pjpuebla.trials.workflow.carpeta.CatalogoEnum;

@Getter
public enum CatalogoEstadoCivil implements CatalogoEnum {
    UNIONLIBRE(0, "Unión libre"),
    DIVORCIADO(1, "Divorciado/a"),
    SEPARADO(2, "Separado/a"),
    SOLTERO(3, "Soltero/a"),
    CONCUBINATO(4, "Concubinato"),
    VIUDO(5, "Viudo/a"),
    CASADO(6, "Casado/a"),
    SOCIEDAD_CONVICENCIA(7, "Sociedad de convivencia"),
    SEPARADO_EN_PROCESO_JUDICIAL(8, "Separado/a en Proceso Judicial"),
    NO_IDENTIFICADO(9, "No identificado");

    private final int clave;
    private final String valor;

    CatalogoEstadoCivil(int clave, String valor) {
        this.clave = clave;
        this.valor = valor;
    }
}
