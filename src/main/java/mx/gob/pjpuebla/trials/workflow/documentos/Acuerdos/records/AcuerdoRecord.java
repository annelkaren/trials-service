package mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records;

import java.time.LocalDate;
import java.util.List;

public record AcuerdoRecord(

    Integer carpetaId,
    Integer documentoId,
    String tipoAcuerdo,
    LocalDate fechaResolucion,
    String etapaProcesal,
    List<String> rubros,
    List<AcuerdoPromocionesRecord> promocionesRelacionadas,
    Character tamanioPapel,
    String textoEditor
) {}
