package mx.gob.pjpuebla.trials.util.enums.carpeta;

import lombok.Getter;

@Getter
public enum CatalogoPosicionTrabajo {

    EMPLEADO(1, "Empleado"),
    OBRERO(2, "Obrero"),
    JORNALERO(3, "Jornalero o peón agrícola"),
    PATRON(4, "Patrón o empresario"),
    COOPERATIVA(5, "Miembro de cooperativa"),
    NO_REMUNERADO(6, "Trabajador no remunerado"),
    CUENTA_PROPIA(7, "Trabajador por cuenta propia"),
    OTRA(8, "Otra");

    private final int id;
    private final String nombre;

    CatalogoPosicionTrabajo(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}
