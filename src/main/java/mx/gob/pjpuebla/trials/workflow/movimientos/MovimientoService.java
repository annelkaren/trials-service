package mx.gob.pjpuebla.trials.workflow.movimientos;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
}
