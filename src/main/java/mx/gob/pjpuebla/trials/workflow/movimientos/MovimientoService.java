package mx.gob.pjpuebla.trials.workflow.movimientos;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;

    public List<MovimientoSalidaRecord> getMovimientosSalida(String uuid){
        return movimientoRepository.salidas(uuid);
    }
}
