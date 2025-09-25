package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum TipoNotificacion {
    ESTRADO("Estrado"),
    CORREO_ELECTRONICO("Correo electrónico"),
    DOMICILIO("Domicilio"),
    NINGUNO("Ninguno"),
    EMPLAZAMIENTO("Domicilio emplazamiento"), //demandado
    EXHORTO("Exhorto"),
    EDICTOS("Edictos");
    
    private final String tipoNotificacion;

    TipoNotificacion(String tipoNotificacion){
        this.tipoNotificacion = tipoNotificacion;
    }
}
