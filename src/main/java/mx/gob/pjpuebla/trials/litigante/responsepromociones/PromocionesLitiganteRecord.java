package mx.gob.pjpuebla.trials.litigante.responsepromociones;

import java.io.Serializable;
import java.util.List;

public record PromocionesLitiganteRecord(
        String numeroExpediente,
        List<PromocionesElectronicasLitigante> promocionesElectronicasLitigante
) implements Serializable {
}
