package mx.gob.pjpuebla.trials.util;

import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.util.ArrayList;
import java.util.List;

public class SearchLikeEnum {

    public static List<Estado> searchByEstadoEnum(String key) {
        List<Estado> estados = new ArrayList<>();
        if (!key.isEmpty()) {
            for (Estado estado : Estado.values()) {
                if (key.equalsIgnoreCase("activo")) {
                    estados.add(Estado.ACTIVE);
                }
                if (key.equalsIgnoreCase("inactivo")) {
                    estados.add(Estado.INACTIVE);
                }
                if (estado.name().toLowerCase().contains(key.toLowerCase())) {
                    estados.add(estado);
                }
            }
        }
        return estados;
    }
}
