package mx.gob.pjpuebla.trials.util.enums;

public enum EstadoCentralComisario {
    PENDIENTE("Pendiente"),
    RECIBIDO("Recibido en Central de comisarios"),
    ASIGNADO("Asignado a comisario"),
    NOTIFICADO("Notificado al comisario"),
    PENDIENTE_DEVOLUCION("Pendiente de devolución"),
    DEVUELTO("Devuelto al juzgado");

    private final String etiqueta;

    EstadoCentralComisario(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
