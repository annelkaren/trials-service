package mx.gob.pjpuebla.trials.core.procedimientos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional(readOnly = true)
    public List<ProcedimientoRecord> getAllByTipoJuicio(Integer tipoJuicioId) {
        List<Procedimiento> procedimientoList = procedimientoRepository.findByTipoJuicioIdAndEstado(tipoJuicioId, Estado.ACTIVE);
        return procedimientoList.stream().map(
                p -> new ProcedimientoRecord(p.getId(), p.getNombre())
        ).toList();
    }
}
