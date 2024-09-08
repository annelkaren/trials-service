package mx.gob.pjpuebla.trials.core.documentos;

import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecord;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRecord;

public record DocumentoRecord(

        Integer id,
        String folio,
        String ruta,
        String procesal,
        String tipoDocumento,
        JuzgadoRecord juzgado,
        TipoJuicioRecord tipoJuicio
) {
}
