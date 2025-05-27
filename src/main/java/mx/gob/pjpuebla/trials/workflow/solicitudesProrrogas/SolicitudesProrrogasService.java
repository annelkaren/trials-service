package mx.gob.pjpuebla.trials.workflow.solicitudesProrrogas;

import java.time.LocalDate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.eventos.EventoService;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.ApiResponse;
import mx.gob.pjpuebla.trials.error.ApiResponseFactory;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.EstadoProrroga;
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class SolicitudesProrrogasService {

    private final SolicitudesProrrogasRepository solicitudesProrrogasRepository;
    private final MovimientoRepository movimientoRepository;
    private final PersonaService personaService;
    private final EventoService eventoService;

    public SolicitudesProrrogas getLastProrrogas(Integer movimientoId) {
        return solicitudesProrrogasRepository.findFirstByMovimientoIdOrderByIdDesc(movimientoId).orElse(null);
    }

    public ResponseEntity<ApiResponse<?>> solicitarProrroga(SolicitudesProrrogasRecord movimientoProrrogaRecord) {

        // Buscar Movimiento:
        Movimiento movimiento = movimientoRepository.findById(movimientoProrrogaRecord.movimientoId())
                .orElseThrow(() -> new NotFoundException("Movimiento no encontrado",
                        "movimientoId: " + movimientoProrrogaRecord.movimientoId()));

        if (!movimientoProrrogaRecord.fechaProrroga().isAfter(LocalDate.now())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseFactory.error("La fecha debe ser posterior al día de hoy",
                            ApiResponseFactory.VALIDATION_ERROR));
        }

        if (eventoService.esDiaInHabil(movimientoProrrogaRecord.fechaProrroga(), null, null)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseFactory.error("La fecha debe ser un dia habil",
                            ApiResponseFactory.VALIDATION_ERROR));
        }

        SolicitudesProrrogas solicitudProrroga = new SolicitudesProrrogas();

        solicitudProrroga.setEstado(EstadoProrroga.SOLICITADA);
        solicitudProrroga.setFechaProrroga(movimientoProrrogaRecord.fechaProrroga());
        solicitudProrroga.setMotivoProrroga(movimientoProrrogaRecord.motivoProrroga());
        solicitudProrroga.setAudit(new Audit());
        solicitudProrroga.setMovimiento(movimiento);

        solicitudesProrrogasRepository.save(solicitudProrroga);

        return ResponseEntity.ok(ApiResponseFactory.success("Prórroga solicitada con éxito"));
    }

    public Page<SolicitudProrrogaRecordResponse> getSolicitudesProrrogas(String key, Pageable pageable) {
        return solicitudesProrrogasRepository.getAll(pageable);
        

    }

    public ResponseEntity<ApiResponse<?>> actualizaSolicitudProrroga(SolicitudesProrrogasSaveRecord solicitudesProrrogasSaveRecord) {
        
        if (eventoService.esDiaInHabil(solicitudesProrrogasSaveRecord.fechaAutorizacion(), null, null)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseFactory.error("La fecha de autorización debe ser un dia habil",
                            ApiResponseFactory.VALIDATION_ERROR));
        }


        SolicitudesProrrogas solicitudesProrroga = solicitudesProrrogasRepository.findById(solicitudesProrrogasSaveRecord.solicitudProrrogaId())
        .orElseThrow(() -> new NotFoundException("Solicitud no encontrada",
                        "solicitudProrrogaId: " + solicitudesProrrogasSaveRecord.solicitudProrrogaId()));;

        solicitudesProrroga.setEstado(solicitudesProrrogasSaveRecord.estadoProrroga());
        solicitudesProrroga.setFechaAutorizada(solicitudesProrrogasSaveRecord.fechaAutorizacion());

        solicitudesProrrogasRepository.save(solicitudesProrroga);
        
        return ResponseEntity.ok(ApiResponseFactory.success("Respuesta enviada con éxito"));
    }
    
}
