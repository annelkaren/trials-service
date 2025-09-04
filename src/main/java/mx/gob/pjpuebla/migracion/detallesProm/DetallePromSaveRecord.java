package mx.gob.pjpuebla.migracion.detallesProm;

import mx.gob.pjpuebla.trials.util.enums.TipoPromocion;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

public record DetallePromSaveRecord(
    Carpeta carpeta, 
    TipoPromocion tipoPromocion,
    String folio,
    String ruta,
    String acuerdo
) {}
