package mx.gob.pjpuebla.trials.workflow.documentos.amparos;

import java.io.Serializable;
import java.time.LocalDate;

import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoImpugnacionAmparo;
import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoSentidoAmparo;

public record AmparoRecord(
    String tipoAmparo,
    LocalDate fechaPresentacion,
    Integer impugnacion,
    CatalogoSentidoAmparo sentidoAmparo,
    CatalogoImpugnacionAmparo impugnacionAmparo,
    String quejoso,
    Integer tribunalId,
    Integer salaId) implements Serializable {
}
