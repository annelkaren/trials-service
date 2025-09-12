package mx.gob.pjpuebla.migracion.acl.mapper;

import java.util.Objects;

import org.springframework.stereotype.Component;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

@Component
public class EstadoOficioMapper {
    
    public EstadoCarpeta estadoOficioMapper(String motivo, String rutaAcuse, String estatusOfi){
        if(estatusOfi.equals("N")){ return EstadoCarpeta.CANCELADO; }
        if(!rutaAcuse.isBlank() || !rutaAcuse.isEmpty()){ return EstadoCarpeta.CON_ACUSE; }
        
        if(Objects.equals(motivo,"Cancelado") || Objects.equals(motivo, "")){ return EstadoCarpeta.CANCELADO; }

        
        return EstadoCarpeta.PUBLICADO;
    }
}
