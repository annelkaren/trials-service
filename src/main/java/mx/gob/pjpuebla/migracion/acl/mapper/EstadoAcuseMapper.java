package mx.gob.pjpuebla.migracion.acl.mapper;

import java.util.Objects;

import org.springframework.stereotype.Component;

import mx.gob.pjpuebla.trials.util.enums.EstadoAcuse;


@Component
public class EstadoAcuseMapper {
    
    public EstadoAcuse mapEstadoAcuse(String motivo, String rutaAcuse){
        if(Objects.equals(motivo, "Cancelado")){
            return EstadoAcuse.CANCELADO;
        }

        if(Objects.equals(motivo, "ENTREGADO")){ return EstadoAcuse.ENTREGADO; }
        
        if(motivo == null || motivo.isBlank() || motivo.isEmpty() || motivo.equals("") ||
            rutaAcuse == null ||  rutaAcuse.isEmpty() || rutaAcuse.isBlank()) {
            return EstadoAcuse.DESCONOCIDO;
        }

        if(!rutaAcuse.isBlank() || !rutaAcuse.isEmpty()){
            return EstadoAcuse.ENTREGADO;
        }

        return null;

    }

}
