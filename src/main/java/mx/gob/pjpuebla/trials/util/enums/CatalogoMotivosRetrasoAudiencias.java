package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum CatalogoMotivosRetrasoAudiencias {
    ACTOR_NO_LLEGO("La parte actora no llegó con la oportunidad solicitada"),
    ACTOR_LLEGO_TARDE("La parte actora llegó tarde"),
    FALLA_EN_SALA("Falla en la sala"),
    RETRASO_AUDIENCIA("Retraso en la audiencia anterior"),
    OTRO("Otro");

    private final String etiqueta;

    CatalogoMotivosRetrasoAudiencias(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}
