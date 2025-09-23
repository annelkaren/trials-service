package mx.gob.pjpuebla.migracion.acl.mapper;

import org.springframework.stereotype.Component;
import mx.gob.pjpuebla.trials.util.enums.TipoPromocion;

/**
 * Mapea tipo de promoción desde legacy.
 * Reglas:
 * - Si descripción dice "PROMOCION ELECTRONICA" → CORREO_ELECTRONICO
 * - Si tipo es null o vacío → ESCRITO
 * - Si tipo ∈ {0,1,3,E} → ESCRITO; si "2" → OFICIO
 */
@Component
public class PromocionMapper {

    public TipoPromocion mapTipoPromocion(String tipoPromocion, String descripcion) {
        // Normaliza entradas
        String desc = descripcion == null ? "" : descripcion.trim();
        String tipo = tipoPromocion == null ? "" : tipoPromocion.trim();

        // Regla 1: descripción manda si es "PROMOCION ELECTRONICA"
        if (desc.equalsIgnoreCase("PROMOCION ELECTRONICA")) {
            return TipoPromocion.CORREO_ELECTRONICO;
        }

        // Regla 2: si tipo vacío => ESCRITO
        if (tipo.isEmpty()) {
            return TipoPromocion.ESCRITO;
        }

        // Regla 3: casos concretos por código
        return "2".equals(tipo) ? TipoPromocion.OFICIO : TipoPromocion.ESCRITO;
    }
}