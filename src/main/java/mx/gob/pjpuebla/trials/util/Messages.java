package mx.gob.pjpuebla.trials.util;

public class Messages {

    private Messages() {
        throw new IllegalStateException("Utility class");
    }

    public static final String UNKNOWN_ERROR = "Error desconocido";
    public static final String OPTIMISTIC_LOCKING_ERROR = "Version modificada por otro usuario";
}
