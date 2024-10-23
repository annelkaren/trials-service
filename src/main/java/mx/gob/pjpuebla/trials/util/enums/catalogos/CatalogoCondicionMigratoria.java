package mx.gob.pjpuebla.trials.util.enums.catalogos;

import lombok.Getter;
import mx.gob.pjpuebla.trials.workflow.carpeta.CatalogoEnum;

@Getter
public enum CatalogoCondicionMigratoria implements CatalogoEnum {
    VISITANTE_SIN_PERMISO_ACTIVIDADES_REMUNERADAS(0, "Visitante sin permiso para realizar actividades remuneradas"),
    VISITANTE_CON_PERMISO_ACTIVIDADES_REMUNERADAS(1, "Visitante con permiso para realizar actividades remuneradas"),
    VISITANTE_REGIONAL(2,"Visitante regional"),
    VISITANTE_TRABAJADOR_FRONTERIZO(3, "Visitante trabajador fronterizo"),
    VISITENTE_RAZONES_HUMANITARIAS(4, "Visitante por razones humanitarias"),
    VISITENTE_FINES_ADOPCION(5, "Visitante con fines de adopción"),
    RESIDENTE_TEMPORAL(6, "Residente temporal"),
    RESIDENTE_TEMPORAL_ESTUDIANTE(7, "Residente temporal estudiante"),
    RESIDENTE_PERMANETE(8, "Residente permanente"),
    SITUACION_MIGRATORIA_IRREGULAR(9, "Situación migratoria irregular"),
    NO_ESPECIFICADO(10, "No especificado"),
    NO_APLICA(11,"No aplica");

    private final int clave;
    private final String valor;

    CatalogoCondicionMigratoria(int clave, String valor) {
        this.clave = clave;
        this.valor = valor;
    }
}
