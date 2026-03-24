package mx.gob.pjpuebla.migracion.acl.mapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import mx.gob.pjpuebla.trials.util.enums.TipoSentencia;

/**
 * Determina el tipo de sentencia (DEFINITIVA/INTERLOCUTORIA) a partir de texto.
 */
@Component
public class SentenciaMapper {

    private static final Pattern P_DEF = Pattern.compile("sentencia\\s+definitiva", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern P_INT = Pattern.compile("sentencia\\s+interlocutoria", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern P_DEF_SOLO = Pattern.compile("\\bdefinitiva\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern P_INT_SOLO = Pattern.compile("\\binterlocutoria\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    public TipoSentencia mapTipoSentencia(String texto) {
        if (texto == null || texto.isBlank() || "sentencia".equalsIgnoreCase(texto.trim())) {
            return TipoSentencia.SENTENCIA_DEFINITIVA;
        }

        int idxDef = firstIndex(P_DEF, texto);
        int idxInt = firstIndex(P_INT, texto);
        if (idxDef != Integer.MAX_VALUE || idxInt != Integer.MAX_VALUE) {
            return (idxInt < idxDef) ? TipoSentencia.SENTENCIA_INTERLOCUTORIA : TipoSentencia.SENTENCIA_DEFINITIVA;
        }

        int idxDef2 = firstIndex(P_DEF_SOLO, texto);
        int idxInt2 = firstIndex(P_INT_SOLO, texto);
        if (idxDef2 == Integer.MAX_VALUE && idxInt2 == Integer.MAX_VALUE) {
            return TipoSentencia.SENTENCIA_DEFINITIVA;
        }
        return (idxInt2 < idxDef2) ? TipoSentencia.SENTENCIA_INTERLOCUTORIA : TipoSentencia.SENTENCIA_DEFINITIVA;
    }

    private static int firstIndex(Pattern p, String text) {
        Matcher m = p.matcher(text);
        return m.find() ? m.start() : Integer.MAX_VALUE;
        }
}