package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum EstadoCarpeta {
    CAPTURA ("Captura"),
    SALIDA ("Salida"),
    RECEPCION ("Recepción"),
    TURNADO ("Turnado"),
    ASIGNADO ("Asignado"),
    DEVUELTO ("Devuelto"),
    CREADO ("Creado"),
    CON_ACUSE ("Con acuse"),
    CANCELADO ("Cancelado"),
    PUBLICADO ("Publicado"),
    NOTIFICADO ("Notificado"),
    INTEGRADO ("Integrado"),//Para indicar que una pieza fue integrada al expediente
    EDICION ("Edición"),
    ARCHIVO_JUDICIAL ("Archivo judicial"),
    DEVUELTO_A_OFICIALIA("Devuelto a oficialía"),
    MIGRADO("Expediente migrado"),
    ARCHIVO_JUDICIAL_RECIBIDO("Archivo judicial recibido"),
    ARCHIVO_JUDICIAL_SOLICIT("Archivo judicial solcitud"),
    CENTRAL_COMISARIOS("Central de comisarios");

    private final String etiqueta;

    EstadoCarpeta(String etiqueta){
        this.etiqueta = etiqueta;
    }
}
