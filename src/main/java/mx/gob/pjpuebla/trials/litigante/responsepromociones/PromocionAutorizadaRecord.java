package mx.gob.pjpuebla.trials.litigante.responsepromociones;

import java.io.Serializable;
import java.util.List;

public record PromocionAutorizadaRecord(
        List<PromocionesLitiganteRecord> expedienteAutorizado
) implements Serializable {
}
