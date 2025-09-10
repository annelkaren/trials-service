package mx.gob.pjpuebla.migracion.acl.mapper;

import org.springframework.stereotype.Component;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

@Component
public class EstadoOficioMapper {
    
    public EstadoCarpeta estadoOficioMapper(String motivo, String rutaAcuse, String estatusOfi){
        if(estatusOfi.equals("N")){ return EstadoCarpeta.CANCELADO; }
        if(motivo.equals("Cancelado")){ return EstadoCarpeta.CANCELADO; }

        if(!rutaAcuse.isBlank() || !rutaAcuse.isEmpty()){ return EstadoCarpeta.CON_ACUSE; }
        
        return EstadoCarpeta.PUBLICADO;
    }
}
