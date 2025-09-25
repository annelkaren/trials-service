package mx.gob.pjpuebla.migracion.acl.mapper;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

/** Transforma una cadena de rubros separados por punto en lista saneada. */
@Component
public class RubrosMapper {
    public List<String> mapRubros(String rubros) {
        if (rubros == null || rubros.isBlank()) return List.of();
        return Arrays.stream(rubros.split("\\."))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();
    }
}