package mx.gob.pjpuebla.migracion.acl.mapper;

import org.springframework.stereotype.Component;

/** Mapea tipos de parte: "A"→Actor, "D"→Demandado. */
@Component
public class PartesMapper {
    public String mapTipoPartesMigracion(String tipo) {
        if (tipo == null) return "";
        return switch (tipo) {
            case "D" -> "Demandado";
            case "A" -> "Actor";
            default -> "";
        };
    }
}