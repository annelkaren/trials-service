package mx.gob.pjpuebla.migracion.acl.mapper;

import org.springframework.stereotype.Component;
import mx.gob.pjpuebla.trials.util.enums.EstadoAcuse;

@Component
public class EstadoAcuseMapper {

    public EstadoAcuse mapEstadoAcuse(String motivo, String rutaAcuse) {
        // 1) Normaliza comparaciones de estado (case-insensitive)
        if ("cancelado".equalsIgnoreCase(motivo)) {
            return EstadoAcuse.CANCELADO;
        }
        if ("entregado".equalsIgnoreCase(motivo)) {
            return EstadoAcuse.ENTREGADO;
        }

        // 2) Valida entradas: isBlank() cubre vacío y solo-espacios; verifica null
      
        if (isBlank(motivo) || isBlank(rutaAcuse)) {
            return EstadoAcuse.DESCONOCIDO;
        }

        // 3) Si hay acuse con texto -> ENTREGADO 
        return EstadoAcuse.ENTREGADO;
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
