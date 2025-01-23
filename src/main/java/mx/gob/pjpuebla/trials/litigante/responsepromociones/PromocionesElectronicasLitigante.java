package mx.gob.pjpuebla.trials.litigante.responsepromociones;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public record PromocionesElectronicasLitigante(
        String numeroPromocionE,
        String usuarioOrigen,
        String nombreArchivo,
        LocalDate fechaSubida,
        LocalTime horaSubida,
        String rutaArchivo
) implements Serializable {
}
