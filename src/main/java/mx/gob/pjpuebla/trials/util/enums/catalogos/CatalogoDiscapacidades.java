package mx.gob.pjpuebla.trials.util.enums.catalogos;

import lombok.Getter;
import mx.gob.pjpuebla.trials.workflow.carpeta.CatalogoEnum;

@Getter
public enum CatalogoDiscapacidades implements CatalogoEnum {
    DISCAPACIDADES_PARA_VER(0, "Discapacidades para ver"),
    DISCAPACIDADES_PARA_OIR(1, "Discapacidades para oir"),
    DISCAPACIDADES_PARA_HABLAR(3, "Discapacidades para hablar (mudez)"),
    DISCAPACIDADES_COMUNICACION_COMPRENSION_LENGUAJE(4, "Discapacidades de la comunicación y comprensión del lenguaje"),
    INCUFICIENTEMENTE_ESPECIFICADAS_GRUPO_DISCAPACIDADES_SENSORIALES_Y_COMUNICACION(4, "Insuficientemente especificadas del grupo de discapacidades sensoriales y de la comunicación"),
    DISCAPACIDADES_EXTREMIDADES_INFERIORES_TRONCO_CUELLO_CABEZA(5, "Discapacidades de las extremidades inferiores, tronco, cuello y cabeza"),
    DISCAPACIDADES_EXTRIMIDADES_SUPERIORES(6, "Discapacidades de las extremidades superiores"),
    INSUFICIENTEMENTE_ESPECIFICADAS_GRUPO_DISCAPACIDADES_MOTRICES(7, "Insuficientemente especificadas del grupo discapacidades motrices"),
    DISCAPACIDADES_INTELECTUALES_RETRASO_MENTAL(8, "Discapacidades intelectuales (retraso mental)"),
    DISCAPACIDADES_CONDUCTALES_OTRAS_MENTALES(9, "Discapacidades conductuales y otras mentales"),
    INSUFICIENTEMENTE_ESPECIFICADAS_GRUPO_DISCAPACIDASES_MENTALES(10, "Insuficientemente especificadas del grupo de discapacidades mentales"),
    DISCAPACIDADES_MULTIPLES(11, "Discapacidades múltiples"),
    OTRO_TIPO_DISCAPACIDADES(12, "Otro tipo de discapacidades"),
    INSUFICIENTEMENTE_ESPECIFICADAS_GRUPO_DISCAPACIDADES_MULTIPLES_OTRAS(13, "Insuficientemente especificadas del grupo de discapacidades múltiples y otras"),
    DESCRIPCIONES_QUE_NO_CORRESPONDEN_AL_CONCEPTO_DISCAPACIDAD(14, "Descripciones que no corresponden al concepto de discapacidad"),
    NO_SABE(15, "No sabe");

    private final int clave;
    private final String valor;

    CatalogoDiscapacidades(int clave, String valor) {
        this.clave = clave;
        this.valor = valor;
    }
}
