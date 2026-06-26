package mx.gob.pjpuebla.trials.util.enums;

public enum InstanciaJuzgado {
    PRIMERA_INSTANCIA("Primera Instancia"),
    SEGUNDA_INSTANCIA("Segunda Instancia"),
    EXHORTO("Exhortos"),
    NO_APLICA("No Aplica"),
    CENTRAL_COMISARIOS("Central de Comisarios"),
    UNIDAD_GESTION("Unidad de Gestión");

    private final String nombre;

    InstanciaJuzgado(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
