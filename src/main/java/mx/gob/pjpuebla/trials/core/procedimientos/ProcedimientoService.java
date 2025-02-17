package mx.gob.pjpuebla.trials.core.procedimientos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ProcedimientoService {

    private final ProcedimientoRepository procedimientoRepository;
    private final TipoJuicioRepository tipoJuicioRepository;

    @Transactional(readOnly = true)
    public List<ProcedimientoRecord> getAllByTipoJuicio(Integer tipoJuicioId) {
        List<Procedimiento> procedimientoList;
        TipoJuicio tipoJuicio = tipoJuicioRepository.findById(tipoJuicioId).orElseThrow(() -> new NotFoundException("Tipo juicio no encontrado", "tipo juicio id: " + tipoJuicioId));
        if (tipoJuicio.getMateria().getNombre().toUpperCase().contains("JUSTICIA PARA ADOLESCENTES") ||
                tipoJuicio.getMateria().getNombre().toUpperCase().contains("PENAL")) {
            procedimientoList = procedimientoRepository.findByTipoJuicioNombreAndEstado("Penal", Estado.ACTIVE);
        } else {
            procedimientoList = procedimientoRepository.findByTipoJuicioIdAndEstado(tipoJuicioId, Estado.ACTIVE);
        }
        return procedimientoList.stream().map(
                p -> new ProcedimientoRecord(p.getId(), p.getNombre())
        ).toList();
    }
}
