package mx.gob.pjpuebla.trials.workflow.documentos.records;


import java.util.List;

public record ExhortoResponseRecord(

        List<String> anexos,
        String exhortoObservaciones,
        String exhortoProcedencia,
        String tipoJuicio
) {
}
