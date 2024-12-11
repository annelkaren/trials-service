package mx.gob.pjpuebla.trials.workflow.documentos.amparos;

import java.io.Serializable;
import java.time.LocalDate;

public record AmparoRecord(
    Integer carpetaId,
    String tipoAmparo,
    LocalDate fechaPresentacion,
    LocalDate fechaTermino,
    Integer impugnacion,
    String sentidoAmparo,
    String sentidoImpugnacion,
    String quejoso,
    Integer tribunalId,
    Integer salaId) implements Serializable {
}
