package mx.gob.pjpuebla.trials.util.enums;

import lombok.Getter;

@Getter
public enum DevolucionMotivo {
    ERROR_EXPEDIENTE(1, "Error en el número de expediente"),
    PIEZAS_INNECESARIAS(2, "Piezas Innecesarias"),
    PASE_ECONOMICO(3, "Pase económico"),
    OTRO(4, "Otro");

    private final int id;
    private final String nombre;

    DevolucionMotivo(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}
