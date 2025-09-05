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
        if (descripcion != null && descripcion.equalsIgnoreCase("PROMOCION ELECTRONICA")) {
            return TipoPromocion.CORREO_ELECTRONICO;
        }

        if (tipoPromocion == null || tipoPromocion.isBlank()) {
            return TipoPromocion.ESCRITO;
        }

        return switch (tipoPromocion) {
            case "2" -> TipoPromocion.OFICIO;
            case "0", "1", "3", "E" -> TipoPromocion.ESCRITO;
            default -> TipoPromocion.ESCRITO;
        };
    }
}