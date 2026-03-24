package mx.gob.pjpuebla.trials.workflow.movimientos;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class H2Jsonb {

    /**
     * Emula Postgres jsonb_extract_path_text(jsonb, key) para H2.
     * Mantén SOLO esta firma para que H2 no se confunda.
     */
    public static String jsonbExtractPathText(String json, String key) {
        if (json == null || key == null) return "";

        // "key": "value"
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher m = p.matcher(json);
        if (m.find()) return m.group(1);

        // "key": value (no string)
        Pattern p2 = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*([^,}\\]]+)");
        Matcher m2 = p2.matcher(json);
        if (m2.find()) return m2.group(1).trim().replaceAll("^\"|\"$", "");

        return "";
    }
}