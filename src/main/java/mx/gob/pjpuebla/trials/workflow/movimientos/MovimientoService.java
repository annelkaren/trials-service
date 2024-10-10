package mx.gob.pjpuebla.trials.workflow.movimientos;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;

    public List<MovimientoSalidaRecord> getMovimientosSalida(String uuid){

        UUID uuidMov = UUID.fromString(uuid);
        return movimientoRepository.salidas(uuidMov, EstadoCarpeta.SALIDA);
    }
}
