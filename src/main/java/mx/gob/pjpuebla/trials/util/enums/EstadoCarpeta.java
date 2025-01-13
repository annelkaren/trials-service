package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum EstadoCarpeta {
    CAPTURA,
    SALIDA,
    RECEPCION,
    TURNADO,
    ASIGNADO,
    DEVUELTO,
    CREADO,
    CON_ACUSE,
    CANCELADO,
    PUBLICADO,
    NOTIFICADO,
    INTEGRADO,//Para indicar que una pieza fue integrada al expediente
    EDICION,
    ARCHIVO_JUDICIAL
}
