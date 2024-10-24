package mx.gob.pjpuebla.trials.util.enums.carpeta;

import lombok.Getter;

@Getter
public enum CatalogoGrupoVulnerable {
    MUJERES("Mujeres"),
    INDIGENAS("Indígenas"),
    GRUPO_LGTBBB("Grupo LGTBBB+"),
    PERSONAS_CON_DISCAPACIDAD("Personas con discapacidad"),
    ADULTO_MAYOR("Adulto mayor"),
    PERIODISTAS_DEFENSORES("Periodistas/Defensores de derechos humanos (Asociación civil)"),
    NO_APLICA("No Aplica");

    private final String etiqueta;

    CatalogoGrupoVulnerable(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}
