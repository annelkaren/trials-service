package mx.gob.pjpuebla.migracion.utils;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.util.enums.TipoPromocion;
import mx.gob.pjpuebla.trials.util.enums.TipoResolucion;
import mx.gob.pjpuebla.trials.util.enums.TipoSentencia;

@RequiredArgsConstructor
@Service
public class UtilsMigracion {

    // Declaramos los posibles valores que necesitamos declarar de tipoSentencia:
    private static final Pattern P_DEF = Pattern.compile("sentencia\\s+definitiva",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern P_INT = Pattern.compile("sentencia\\s+interlocutoria",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    private static final Pattern P_DEF_SOLO = Pattern.compile("\\bdefinitiva\\b",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern P_INT_SOLO = Pattern.compile("\\binterlocutoria\\b",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    // mapeos
    public String mapMateria(String m) {
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

    public String mapTipoPersona(String tipoPersona) {
        return switch (tipoPersona) {
            case "F" -> "fisica";
            case "M" -> "moral";
            default -> "";
        };
    }

    public String mapTipoPartesMigracion(String tipo) {
        return switch (tipo) {
            case "D" -> "Demandado";
            case "A" -> "Actor";
            default -> "";
        };
    }

    public TipoNotificacion mapTipoNotificacion(String tipoNotificacion) {
        if (tipoNotificacion == null) {
            return TipoNotificacion.NINGUNO;
        }

        return switch (tipoNotificacion) {
            case "CO" -> TipoNotificacion.CORREO_ELECTRONICO;
            case "DN" -> TipoNotificacion.DOMICILIO;
            case "DE" -> TipoNotificacion.EMPLAZAMIENTO;
            case "ES", "E" -> TipoNotificacion.ESTRADO;
            case "EX" -> TipoNotificacion.EXHORTO;
            case "ED" -> TipoNotificacion.EDICTOS;
            default -> TipoNotificacion.NINGUNO;
        };
    }

    public TipoPromocion mapTipoPromocion(String tipoPromocion, String descripcion) {
        // 1) Si la descripción indica promoción electrónica
        if ("PROMOCION ELECTRONICA".equalsIgnoreCase(descripcion)) {
            return TipoPromocion.CORREO_ELECTRONICO;
        }

        // 2) Si tipoPromocion no viene o viene vacío/espacios ⇒ ESCRITO
        if (tipoPromocion == null || tipoPromocion.isBlank()) {
            return TipoPromocion.ESCRITO;
        }

        // 3) Normaliza y decide
        String code = tipoPromocion.trim();
        if ("2".equals(code)) {
            return TipoPromocion.OFICIO;
        }
        return TipoPromocion.ESCRITO;
    }

    public List<String> mapRubros(String rubros) {
        if (rubros == null || rubros.isBlank()) {
            return List.of();
        }

        return Arrays.stream(rubros.split("\\."))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public TipoSentencia mapTipoSentencia(String tipoSentencia) {

        // SI la cadena es nula, esta vacia o dice solo sentencia asignamos sentencia
        // definitiva.
        if (tipoSentencia == null || tipoSentencia.isBlank() || tipoSentencia.equalsIgnoreCase("sentencia")) {
            return TipoSentencia.SENTENCIA_DEFINITIVA;
        }

        // 1) Buscamos las grases completas y tomar la que aparezca primero en la
        // cadena:
        int idxDef = firstIndex(P_DEF, tipoSentencia);
        int idxInt = firstIndex(P_INT, tipoSentencia);

        if (idxDef != Integer.MAX_VALUE || idxInt != Integer.MAX_VALUE) {
            return (idxInt < idxDef) ? TipoSentencia.SENTENCIA_INTERLOCUTORIA
                    : TipoSentencia.SENTENCIA_DEFINITIVA;
        }

        // 2) Fallback: si no se encontró "sentencia ..." pero sí las palabras clave
        // sueltas
        int idxDef2 = firstIndex(P_DEF_SOLO, tipoSentencia);
        int idxInt2 = firstIndex(P_INT_SOLO, tipoSentencia);

        if (idxDef2 == Integer.MAX_VALUE && idxInt2 == Integer.MAX_VALUE) {
            // Si nada coincide, puedes devolver un default o DESCONOCIDA según tu dominio
            return TipoSentencia.SENTENCIA_DEFINITIVA;
        }
        return (idxInt2 < idxDef2) ? TipoSentencia.SENTENCIA_INTERLOCUTORIA
                : TipoSentencia.SENTENCIA_DEFINITIVA;

    }

    public TipoResolucion mapTipoResolucionSentencia(String tipoResolucion) {
        return switch (tipoResolucion) {
            case "A" -> TipoResolucion.ABSOLUTORIA;
            case "C" -> TipoResolucion.CONDENATORIA;
            case "D" -> TipoResolucion.DECLARATIVA;
            case "I" -> TipoResolucion.IMPROCEDENTE;
            default -> throw new NotFoundException("No se puede determinar el tipo de resolución de una sentencia",
                    tipoResolucion);
        };
    }

    private static int firstIndex(Pattern p, String text) {
        Matcher m = p.matcher(text);
        return m.find() ? m.start() : Integer.MAX_VALUE;
    }

    // -------- Helpers internos --------

    public String normalizeExpediente(String expediente) {
        if (expediente == null || expediente.isBlank()) {
            throw new IllegalArgumentException("El 'expediente' no puede ser nulo ni vacío.");
        }
        return expediente.trim();
    }

    public void requireNonNullJuzgado(Juzgado juzgado) {
        if (juzgado == null) {
            throw new IllegalArgumentException("El 'juzgado' no puede ser nulo.");
        }
    }

    public static String normalizeSpaces(String s) {
        if (s == null)
            return "";
        // Normaliza Unicode
        String x = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFKC);
        // Reemplaza tabs, NBSP, BOM y otros whitespace invisibles
        x = x.replaceAll("[\\u0009\\u00A0\\u200B-\\u200D\\uFEFF]", " ");
        // Quita espacios (de cualquier tipo) al inicio y final
        x = x.replaceAll("^[\\p{Z}\\s]+|[\\p{Z}\\s]+$", "");
        // Colapsa múltiples espacios
        x = x.replaceAll("\\s+", " ");
        return x;
    }
}
