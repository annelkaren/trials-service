package mx.gob.pjpuebla.trials.util;

public class Messages {

    private Messages() {
        throw new IllegalStateException("Utility class");
    }

    public static final String UNKNOWN_ERROR = "Error desconocido";
    public static final String INVALID_TOKEN = "Token invalido";
    public static final String OPTIMISTIC_LOCKING_ERROR = "Version modificada por otro usuario";
    public static final String CONSTRAINT_ERROR = "El registro no puede eliminarse, está siendo utilizado por otros registros, se recomienda desactivarlo.";
}
