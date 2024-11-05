package mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records;

import java.time.LocalDate;
import java.util.List;

public record AcuerdoRecord(
    Integer carpetaId,
    String tipoAcuerdo,
    LocalDate fechaResolucion,
    String etapaProcesal,
    List<String> rubros,
    List<Integer> promocionesRelacionadas,
    String[] recomendaciones,
    String tamanioPapel,
    String textoEditor,
    String acuedoPublicado
) {}
