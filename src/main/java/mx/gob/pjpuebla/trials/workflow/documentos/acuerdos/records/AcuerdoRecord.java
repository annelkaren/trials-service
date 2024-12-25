package mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record AcuerdoRecord(
    Integer acuerdoId,
    Integer carpetaId,
    Integer documentoId,
    String tipoAcuerdo,
    LocalDate fechaResolucion,
    String etapaProcesal,
    List<String> rubros,
    List<AcuerdoPromocionesRecord> promocionesRelacionadas,
    Character tamanioPapel,
    String textoEditor,
    String resumen
) implements Serializable {}
