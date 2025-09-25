package mx.gob.pjpuebla.migracion.acl.mapper;

public class AmparoMapper {

    public String mapTipoAmparo(String tipo) {
        if (tipo == null) {
            return null;
        }
        return switch (tipo) {
            case "I" -> "AI"; // INDIRECTO
            case "D" -> "AD"; // DIRECTO
            default -> null;
        };
    }

    public String mapSentido(String sentido) {
        if (sentido == null) {
            return null;
        }
        return switch (sentido) {
            case "C" -> "CONCEDE";
            case "N" -> "NIEGA";
            case "S" -> "SOBRESEE";
            case "E" -> "EFECTOS";
            case "D" -> "DESECHA";
            default -> null;
        };
    }

    public Integer mapImpugnacion(String impugnacion) {
        if (impugnacion == null) {
            return null;
        }
        return switch (impugnacion) {
            case "SI" -> 1;
            case "NO" -> 0;
            default -> null;
        };
    }

    public String mapSentidoImpugnacion(String sentidoImpugnacion) {
        if (sentidoImpugnacion == null) {
            return null;
        }
        return switch (sentidoImpugnacion) {
            case "Confirma" -> "CONFIRMA";
            case "Modifica" -> "MODIFICA";
            case "Revoca" -> "REVOCA";
            default -> "";
        };
    }

}
