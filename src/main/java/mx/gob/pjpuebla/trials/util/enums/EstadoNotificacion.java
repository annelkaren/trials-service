package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum EstadoNotificacion {
    PENDIENTE_DE_ASIGNAR,
    ASIGNADO,
    COMPLETADO,
    POR_LEER,
    POR_NOTIFICAR,
    EN_RUTA,
    NOTIFICADOS
}
