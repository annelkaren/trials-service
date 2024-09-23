package mx.gob.pjpuebla.trials.core.juzgados;

import lombok.Data;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRecord;

import java.util.List;

@Data
public class JuzgadoDTO {
    private Juzgado juzgado;
    private List<TipoJuicioRecord> tipoJuicio;
}
