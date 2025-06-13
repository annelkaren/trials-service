package mx.gob.pjpuebla.trials.litigante.responselitigante;

import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRecord;

import java.time.LocalDateTime;
import java.util.List;

public record ExhortoRecord(
        String juzgado,
        String expediente,
        LocalDateTime turnado,
        String procedencia,
        String asignado,
        List<TipoPartesRecord> partes,
        List<HistorialRecord> historial,
        List<PiezaRecord> piezas,
        String cargo
) {
}
