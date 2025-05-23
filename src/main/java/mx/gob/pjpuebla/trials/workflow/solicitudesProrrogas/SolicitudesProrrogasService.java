package mx.gob.pjpuebla.trials.workflow.solicitudesProrrogas;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.EstadoProrroga;
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class SolicitudesProrrogasService {

    private final SolicitudesProrrogasRepository solicitudesProrrogasRepository;
    private final MovimientoRepository movimientoRepository;


    public SolicitudesProrrogas getLastProrrogas(Integer movimientoId){
        return solicitudesProrrogasRepository.findFirstByMovimientoIdOrderByIdDesc(movimientoId).orElse(null);
    }

     public ResponseEntity<?> solicitarProrroga(SolicitudesProrrogasRecord movimientoProrrogaRecord) {
    
        //Buscar Movimiento:
        Movimiento movimiento = movimientoRepository.findById(movimientoProrrogaRecord.motivoId())
                .orElseThrow(() -> new NotFoundException("Movimiento no encontrado", "movimientoId: " + movimientoProrrogaRecord.motivoId()));

        //Crrar solicitud de prorroga

       SolicitudesProrrogas solicitudProrroga = new SolicitudesProrrogas()
        .setEstado(EstadoProrroga.SOLICITADA)
        .setFechaAutorizada(movimientoProrrogaRecord.fechaProrroga())
        .setMotivoProrroga(movimientoProrrogaRecord.motivoProrroga())
        .setMovimiento(movimiento);

        solicitudesProrrogasRepository.save(solicitudProrroga);
        

        return ResponseEntity.ok("Prorroga solicitada");
    }
}
