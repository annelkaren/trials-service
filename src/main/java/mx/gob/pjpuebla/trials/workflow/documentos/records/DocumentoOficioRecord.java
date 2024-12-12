package mx.gob.pjpuebla.trials.workflow.documentos.records;
import java.io.Serializable;
import java.time.LocalDate;

public record DocumentoOficioRecord(
    Integer institucionId,
    LocalDate fechaEmision,
    String asunto,
    Integer carpetaId,
    Integer juzgado
) implements Serializable {
    
}
