package mx.gob.pjpuebla.migracion.acl.mapper;

import org.springframework.stereotype.Component;

import mx.gob.pjpuebla.trials.util.enums.EstadoAcuse;


@Component
public class EstadoAcuseMapper {
    
    public EstadoAcuse mapEstadoAcuse(String motivo, String rutaAcuse){
        if(motivo.isBlank() || motivo.isEmpty() || motivo == "" ||
             rutaAcuse.isEmpty() || rutaAcuse.isBlank()) {
            return EstadoAcuse.DESCONOCIDO;
        }

        if(!rutaAcuse.isBlank() || !rutaAcuse.isEmpty()){
            return EstadoAcuse.ENTREGADO;
        }

        return switch(motivo) {
            case "Cancelado" -> EstadoAcuse.CANCELADO;
            case "ENTREGADO" -> EstadoAcuse.ENTREGADO; 
            default -> EstadoAcuse.NOENTREGADO;
        };
    }

}
