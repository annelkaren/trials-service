package mx.gob.pjpuebla.trials.workflow.transferencias;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.EstadoTransferencia;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoAsignadoResponseRecord;
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoRepository;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
import mx.gob.pjpuebla.trials.workflow.transferencias.records.TransferenciaRecord;
import mx.gob.pjpuebla.trials.workflow.transferencias.records.TransferenciaRecordResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Service
public class TransferenciaServices {
    private final TransferenciaRepository transferenciaRepository;
    private final PersonaService personaService;
    private final PersonaRepository personaRepository;
    private final DocumentoRepository documentoRepository;
    private final MovimientoRepository movimientoRepository;

    public TransferenciaRecordResponse create(TransferenciaRecord record){
        Persona auditor = personaService.getAuditor();
        Persona personaEntrega = personaRepository.findById(record.personaEntregaId().longValue()).orElseThrow(()-> new NotFoundException("La persona no existe", "personaEntregaId"));

        Transferencia transferencia = new Transferencia();

        transferencia.setEntregaId(personaEntrega.getId());
        transferencia.setAutorizaId(auditor.getId());
        transferencia.setEstatus(EstadoTransferencia.AUTORIZADO);
        transferencia.setJuzgado(personaEntrega.getJuzgado());
        transferencia.setTotalExpediente(0);

        transferencia = transferenciaRepository.save(transferencia);

        return  new TransferenciaRecordResponse(transferencia.getId(),
                transferencia.getEntregaId().intValue(),
                null,
                null,
                0,
                null,
                null,
                transferencia.getEstatus().name());

    }

    public TransferenciaRecordResponse update(TransferenciaRecord record, Integer transferenciaId){
        Transferencia transferencia = transferenciaRepository.findById(transferenciaId).orElseThrow(()->new NotFoundException("La transferencia no existe","transferenciaId"));
        Persona personaRecibe = personaRepository.findById(record.personaRecibeId().longValue()).orElseThrow(()-> new NotFoundException("La persona no existe","personaRecibeId"));
        Persona personaEntrega = personaRepository.findById(record.personaEntregaId().longValue()).orElseThrow(()-> new NotFoundException("La persona no existe","personaEntregaId"));
        UUID uuid = UUID.randomUUID();
        LocalDateTime fechaTransferencia = LocalDateTime.now();

        List<Movimiento> asignaciones = documentoRepository.findByPersonaAsignada(null, personaRecibe.getJuzgado().getId(), personaRecibe, Boolean.FALSE, Pageable.unpaged()).getContent();

        for(Movimiento asignacion : asignaciones){
            Movimiento movimiento = new Movimiento()
                    .setCarpeta(asignacion.getCarpeta())
                    .setPersona(personaEntrega)
                    .setDestino(personaRecibe)
                    .setJuzgado(asignacion.getJuzgado())
                    .setMotivo("TRANSFERENCIA")
                    .setFechaAsignacion(fechaTransferencia)
                    .setUuid(uuid)
                    .setObservaciones(record.observaciones())
                    .setRecomendaciones(asignacion.getRecomendaciones())
                    .setEstado(EstadoCarpeta.TURNADO.name())
                    .setConcepto(asignacion.getConcepto())
                    .setDuracion(asignacion.getDuracion());

            movimientoRepository.save(movimiento);
        }

        transferencia.setFechaTransferencia(fechaTransferencia)
                .setTotalExpediente(asignaciones.size())
                .setUuid(uuid)
                .setEstatus(EstadoTransferencia.CONCLUIDO)
                .setObservaciones(record.observaciones());

        transferencia = transferenciaRepository.save(transferencia);

        return new TransferenciaRecordResponse(transferencia.getId(),
                transferencia.getEntregaId().intValue(),
                transferencia.getRecibeId().intValue(),
                transferencia.getFechaTransferencia(),
                transferencia.getTotalExpediente(),
                null,
                uuid.toString(),
                transferencia.getEstatus().name());

    }

    public TransferenciaRecordResponse getTransferencia(String uuid){
        Transferencia transferencia = transferenciaRepository.findByUuuid(UUID.fromString(uuid)).orElseThrow(()-> new NotFoundException("La transferencia no existe","UUID"));
        Persona persona = personaRepository.findById(transferencia.getRecibeId()).orElseThrow(()-> new NotFoundException("La persona no existe","personaRecibeId"));

        List<Movimiento> asignaciones = documentoRepository.findByPersonaAsignada(null, transferencia.getJuzgado().getId(), persona, Boolean.FALSE, Pageable.unpaged()).getContent();

        List<DocumentoAsignadoResponseRecord> transferidos = asignaciones.stream().map(
                (a)-> new DocumentoAsignadoResponseRecord(
                        a.getCarpeta().getId(),
                        a.getCarpeta().getId(),
                        a.getCarpeta().getExpediente(),
                        a.getCarpeta().getFolio(),
                        a.getMotivo(),
                        a.getConcepto(),
                        a.getFechaAsignacion(),
                        a.getFechaAsignacion().plusDays(Long.parseLong(a.getDuracion())),
                        a.getEstado(),
                        a.getObservaciones()
                )).toList();

        return new TransferenciaRecordResponse(transferencia.getId(),
                transferencia.getEntregaId().intValue(),
                transferencia.getRecibeId().intValue(),
                transferencia.getFechaTransferencia(),
                transferencia.getTotalExpediente(),
                transferidos,
                transferencia.getUuid().toString(),
                transferencia.getEstatus().name());
    }

}
