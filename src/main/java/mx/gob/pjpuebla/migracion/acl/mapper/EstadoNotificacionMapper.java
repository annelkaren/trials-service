package mx.gob.pjpuebla.migracion.acl.mapper;

import mx.gob.pjpuebla.trials.util.enums.EstadoNotificacion;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;

public class EstadoNotificacionMapper {
    
    
    public EstadoNotificacion mapEstadoNotificacion(TipoNotificacion tipo) {
        
        switch (tipo) {
            case ESTRADO:
            case CORREO_ELECTRONICO:
            case DOMICILIO:
            case EMPLAZAMIENTO:
            case NINGUNO:
            case EXHORTO:
            case EDICTOS:
            default:
                return null;
        }

    }

}
