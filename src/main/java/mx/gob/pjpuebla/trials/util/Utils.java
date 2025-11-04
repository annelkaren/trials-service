package mx.gob.pjpuebla.trials.util;

public  final class Utils {
   /**
     * Normaliza un expediente quitando los ceros iniciales.
     *
     * Ejemplos:
     *  - "000123" → "123"
     *  - "00100" → "100"
     *  - "000" → "0"
     *
     * @param expediente el expediente a normalizar
     * @return el expediente sin ceros iniciales o cadena vacía si es nulo o vacío
     */
    public static String normalizarExpediente(String expediente) {
        if (expediente == null || expediente.isBlank()) {
            return "";
        }
        return expediente.strip().replaceFirst("^0+(?!$)", "");
    }
}
