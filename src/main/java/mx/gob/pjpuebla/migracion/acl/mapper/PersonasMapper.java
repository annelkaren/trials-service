package mx.gob.pjpuebla.migracion.acl.mapper;

import org.springframework.stereotype.Component;

/**
 * Mapea atributos de personas (tipoPersona, tipo de parte) desde legacy.
 */
@Component
public class PersonasMapper {

    /** "F"→"fisica", "M"→"moral". */
    public String mapTipoPersona(String tipoPersona) {
        if (tipoPersona == null) return "";
        return switch (tipoPersona) {
            case "F" -> "fisica";
            case "M" -> "moral";
            default -> "";
        };
    }
}