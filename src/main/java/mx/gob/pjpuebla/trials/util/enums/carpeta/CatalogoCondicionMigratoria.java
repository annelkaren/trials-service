package mx.gob.pjpuebla.trials.util.enums.carpeta;

import lombok.Getter;

@Getter
public enum CatalogoCondicionMigratoria {
    VIS_SIN_PER_ACT_REM("Visitante sin permiso para realizar actividades remuneradas"),
    VIS_CON_PER_ACT_REM("Visitante con permiso para realizar actividades remuneradas"),
    VIS_REGIONAL("Visitante regional"),
    VIS_TRAB_FRONTERIZO("Visitante trabajador fronterizo"),
    VIS_HUMANITARIAS("Visitante por razones humanitarias"),
    VIS_FINES_ADOPCION("Visitante con fines de adopción"),
    RES_TEMP("Residente temporal"),
    RES_TEMP_EST("Residente temporal estudiante"),
    RES_PERMANETE("Residente permanente"),
    SIT_MIG_IRREGULAR("Situación migratoria irregular"),
    NO_ESPECIFICADO("No especificado"),
    NO_APLICA("No aplica");

    private final String etiqueta;

    CatalogoCondicionMigratoria(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}
