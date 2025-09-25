package mx.gob.pjpuebla.migracion.acl.mapper;

import org.springframework.stereotype.Component;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;

/** Mapea códigos legacy → {@link TipoNotificacion}. */
@Component
public class NotificacionMapper {

    public TipoNotificacion mapTipoNotificacion(String tipo) {
        if (tipo == null) return TipoNotificacion.NINGUNO;
        return switch (tipo) {
            case "CO" -> TipoNotificacion.CORREO_ELECTRONICO;
            case "DN" -> TipoNotificacion.DOMICILIO;
            case "DE" -> TipoNotificacion.EMPLAZAMIENTO;
            case "ES", "E" -> TipoNotificacion.ESTRADO;
            case "EX" -> TipoNotificacion.EXHORTO;
            case "ED" -> TipoNotificacion.EDICTOS;
            default -> TipoNotificacion.NINGUNO;
        };
    }
}