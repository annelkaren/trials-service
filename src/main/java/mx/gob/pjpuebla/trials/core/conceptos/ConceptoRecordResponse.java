package mx.gob.pjpuebla.trials.core.conceptos;

import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecordItem;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.TipoConcepto;

public record ConceptoRecordResponse(
        Integer id,
        String nombre,
        Integer dias,
        TipoConcepto tipoConcepto,
        JuzgadoRecordItem juzgado,
        Estado estado
) {
}
