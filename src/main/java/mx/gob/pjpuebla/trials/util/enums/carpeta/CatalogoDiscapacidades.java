package mx.gob.pjpuebla.trials.util.enums.carpeta;

import lombok.Getter;

@Getter
public enum CatalogoDiscapacidades {
    DISC_VER("Discapacidades para ver"),
    DISC_OIR("Discapacidades para oír"),
    DISC_HABLAR("Discapacidades para hablar (mudez)"),
    DISC_LENGUAJE("Discapacidades de la comunicación y comprensión del lenguaje"),
    IN_ESPEC_DISC_SENSORIALES("Insuficientemente especificadas del grupo de discapacidades sensoriales y de la comunicación"),
    DISC_EXTREM_INF("Discapacidades de las extremidades inferiores, tronco, cuello y cabeza"),
    DISC_EXTREM_SUP("Discapacidades de las extremidades superiores"),
    IN_ESPEC_DISC_MOTRICES("Insuficientemente especificadas del grupo discapacidades motrices"),
    DISC_INTELECTUALES("Discapacidades intelectuales (retraso mental)"),
    DISC_CONDUCTALES("Discapacidades conductuales y otras mentales"),
    IN_ESPEC_DISC_MENTALES("Insuficientemente especificadas del grupo de discapacidades mentales"),
    DISC_MULTIPLES("Discapacidades múltiples"),
    OTRO_TIPO("Otro tipo de discapacidades"),
    IN_ESPEC_DISC_MULTIPLES("Insuficientemente especificadas del grupo de discapacidades múltiples y otras"),
    NO_CORRESPONDEN_DISC("Descripciones que no corresponden al concepto de discapacidad"),
    NO_SABE("No sabe");

    private final String etiqueta;

    CatalogoDiscapacidades(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}
