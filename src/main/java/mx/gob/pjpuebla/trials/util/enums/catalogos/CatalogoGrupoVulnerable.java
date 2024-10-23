package mx.gob.pjpuebla.trials.util.enums.catalogos;

import lombok.Getter;
import mx.gob.pjpuebla.trials.workflow.carpeta.CatalogoEnum;

@Getter
public enum CatalogoGrupoVulnerable implements CatalogoEnum {
    MUJERES(0, "Mujeres"),
    INDIGENAS(1, "Indígenas"),
    GRUPO_LGTBBB(2, "Grupo LGTBBB+"),
    PERSONAS_CON_DISCAPACIDAD(3, "Personas con discapacidad"),
    ADULTO_MAROR(4, "Adulto mayor"),
    PERIODISTAS_DEFENSORES(5, "Periodistas/Defensores de derechos humanos (Asociación civil)"),
    NO_APLICA(6, "No Aplica");

    private final int clave;
    private final String valor;

    CatalogoGrupoVulnerable(int clave, String valor) {
        this.clave = clave;
        this.valor = valor;
    }
}
