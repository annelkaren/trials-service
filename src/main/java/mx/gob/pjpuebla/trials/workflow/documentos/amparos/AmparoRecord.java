package mx.gob.pjpuebla.trials.workflow.documentos.amparos;

import java.io.Serializable;
import java.time.LocalDate;

import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoImpugnacionAmparo;
import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoSentidoAmparo;

public record AmparoRecord(
    Integer carpetaId,
    String tipoAmparo,
    LocalDate fechaPresentacion,
    Integer impugnacion,
    String sentidoAmparo,
    String impugnacionAmparo,
    String quejoso,
    Integer tribunalId,
    Integer salaId) implements Serializable {
}
