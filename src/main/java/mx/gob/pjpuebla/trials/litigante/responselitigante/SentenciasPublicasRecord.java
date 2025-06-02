package mx.gob.pjpuebla.trials.litigante.responselitigante;

import mx.gob.pjpuebla.trials.util.enums.TipoSentencia;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

public record SentenciasPublicasRecord(
        Integer id,
        String expediente,
        String juzgado,
        TipoSentencia tipoSentencia,
        String tipoSentenciaNombre,
        String materia,
        LocalDate fechaResolucion
) {

    public SentenciasPublicasRecord format() {

        return new SentenciasPublicasRecord(id(), expediente(), juzgado(), tipoSentencia(),
                StringUtils.capitalize(tipoSentencia().name().replace("_", " ").toLowerCase()),
                StringUtils.capitalize(materia().toLowerCase()),
                fechaResolucion());
    }
}
