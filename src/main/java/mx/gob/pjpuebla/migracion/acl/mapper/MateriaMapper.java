package mx.gob.pjpuebla.migracion.acl.mapper;

import org.springframework.stereotype.Component;

/**
 * Mapea códigos de materia del sistema legacy a nombres canónicos del sistema
 * actual.
 */
@Component
public class MateriaMapper {

    public String mapMateria(String m) {
        if (m == null)
            return "DESCONOCIDO";
        return switch (m) {
            case "P" -> "PENAL";
            case "L" -> "LABORAL";
            case "M" -> "MERCANTIL";
            case "F" -> "FAMILIAR";
            case "C" -> "CIVIL";
            case "E" -> "EXHORTO";
            case "J" -> "JUSTICIA PARA ADOLESCENTES";
            default -> "DESCONOCIDO";
        };
    }
}