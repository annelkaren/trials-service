package mx.gob.pjpuebla.trials.workflow.transferencias;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.EstadoTransferencia;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoService;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoAsignadoResponseRecord;
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoRepository;
import mx.gob.pjpuebla.trials.workflow.transferencias.records.TransferenciaRecord;
import mx.gob.pjpuebla.trials.workflow.transferencias.records.TransferenciaRecordResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Transactional
@RequiredArgsConstructor
@Service
public class TransferenciaService {
    private final TransferenciaRepository transferenciaRepository;
    private final PersonaService personaService;
    private final PersonaRepository personaRepository;
    private final DocumentoRepository documentoRepository;
    private final MovimientoRepository movimientoRepository;
    private final DocumentoService documentoService;
    private final RoleService roleService;
    private final CarpetaRepository carpetaRepository;

    public TransferenciaRecordResponse create(TransferenciaRecord recordTransferencia){
        Persona auditor = personaService.getAuditor();
        Persona personaEntrega = personaRepository.findById(recordTransferencia.personaEntregaId().longValue()).orElseThrow(()-> new NotFoundException("La persona no existe", "personaEntregaId"));

        Transferencia transferencia = new Transferencia();

        transferencia.setEntregaId(personaEntrega.getId());
        transferencia.setRecibeId(null);
        transferencia.setAutorizaId(auditor.getId());
        transferencia.setEstatus(EstadoTransferencia.AUTORIZADO);
        transferencia.setJuzgado(personaEntrega.getJuzgado());
        transferencia.setTotalExpediente(0);

        Transferencia result = transferenciaRepository.save(transferencia);

        return createRecordResponse(result, personaEntrega, null);

    }

    public TransferenciaRecordResponse update(TransferenciaRecord transferenciaRecord, Integer transferenciaId){
        Transferencia transferencia = transferenciaRepository.findById(transferenciaId).orElseThrow(()->new NotFoundException("La transferencia no existe","transferenciaId"));
        Persona personaRecibe = personaRepository.findById(transferenciaRecord.personaRecibeId().longValue()).orElseThrow(()-> new NotFoundException("La persona no existe","personaRecibeId"));
        Persona personaEntrega = personaRepository.findById(transferenciaRecord.personaEntregaId().longValue()).orElseThrow(()-> new NotFoundException("La persona no existe","personaEntregaId"));
        UUID uuid = UUID.randomUUID();
        LocalDateTime fechaTransferencia = LocalDateTime.now();

        List<Movimiento> asignaciones = documentoRepository.findByPersonaAsignada("", personaEntrega.getJuzgado().getId(), personaEntrega, Boolean.TRUE, Pageable.unpaged(), null, null, null, null, null).getContent();

        for(Movimiento asignacion : asignaciones){
            Movimiento movimiento = new Movimiento()
                    .setCarpeta(asignacion.getCarpeta())
                    .setPersona(personaEntrega)
                    .setDestino(personaRecibe)
                    .setJuzgado(asignacion.getJuzgado())
                    .setMotivo("TRANSFERENCIA")
                    .setFechaAsignacion(fechaTransferencia)
                    .setUuid(uuid)
                    .setObservaciones(transferenciaRecord.observaciones())
                    .setRecomendaciones(asignacion.getRecomendaciones())
                    .setEstado(EstadoCarpeta.TURNADO.name())
                    .setConcepto(asignacion.getConcepto())
                    .setCargo(personaEntrega.getRolPrincipal())
                    .setDuracion(asignacion.getDuracion());

            movimientoRepository.save(movimiento);

            Carpeta carpeta = carpetaRepository.findById(asignacion.getCarpeta().getId()).orElseThrow(()-> new NotFoundException("La carpeta no existe","carpetaId"));
            carpeta.setPersona(personaRecibe);
            carpetaRepository.save(carpeta);
        }

        transferencia.setFechaTransferencia(fechaTransferencia)
                .setTotalExpediente(asignaciones.size())
                .setUuid(uuid)
                .setEstatus(EstadoTransferencia.CONCLUIDO)
                .setObservaciones(transferenciaRecord.observaciones())
                .setRecibeId(personaRecibe.getId());


        Transferencia result = transferenciaRepository.save(transferencia);

        return createRecordResponse(result, personaEntrega, personaRecibe);
    }

    public TransferenciaRecordResponse getTransferencia(String uuid){
        UUID uuidMov = UUID.fromString(uuid);
        Transferencia transferencia = transferenciaRepository.findByUuid(uuidMov).orElseThrow(()-> new NotFoundException("La transferencia no existe","UUID"));
        Persona persona = personaRepository.findById(transferencia.getEntregaId()).orElseThrow(()-> new NotFoundException("La persona no existe","personaEntregaId"));
        Persona personaRecibe = personaRepository.findById(transferencia.getRecibeId()).orElseThrow(()-> new NotFoundException("La persona no existe","personaRecibeId"));

        return createRecordResponse(transferencia, persona, personaRecibe);
    }

    public TransferenciaRecordResponse getTransferenciaByPersonaEntrega(){
        Persona persona = personaService.getAuditor();

        Transferencia transferencia = transferenciaRepository.findByEntregaIdAndEstatus(persona.getId().intValue(), EstadoTransferencia.AUTORIZADO).orElseThrow(()-> new NotFoundException("La transferencia no existe o fue completada","personaEntregaId"));
        Optional<Persona> personaRecibe = personaRepository.findById(transferencia.getRecibeId()!=null? transferencia.getRecibeId() : 0);

        return createRecordResponse(transferencia, persona, personaRecibe.orElse(null));
    }

    private TransferenciaRecordResponse createRecordResponse(Transferencia transferencia, Persona personaEntrega, Persona personaRecibeTmp){
        List<DocumentoAsignadoResponseRecord> transferidos = documentoService.getAllAsignado(personaEntrega, null);
        Optional<Persona> personaRecibe = personaRecibeTmp!=null?Optional.of(personaRecibeTmp):Optional.empty();

        if (transferencia.getEstatus()==EstadoTransferencia.CONCLUIDO){
            transferidos = documentoService.getAllAsignado(personaRecibe.orElse(null), transferencia.getUuid().toString());
        }

        String rolPrincipal = roleService.getRolesByUserId(personaEntrega.getUsuario()).get(0).name();
        String rolRecibe = personaRecibe.isPresent()?roleService.getRolesByUserId(personaRecibe.get().getUsuario()).get(0).name():"";

        return new TransferenciaRecordResponse(
                transferencia.getId(),
                transferencia.getEntregaId(),
                String.format("%s %s %s", personaEntrega.getNombre(),personaEntrega.getApellidoPaterno(), Objects.toString(personaEntrega.getApellidoMaterno(),"")).trim(),
                rolPrincipal,
                transferencia.getRecibeId(),
                personaRecibe.map(value -> String.format("%s %s %s", value.getNombre(), value.getApellidoPaterno(), Objects.toString(value.getApellidoMaterno(), "")).trim()).orElse(""),
                rolRecibe,
                transferencia.getJuzgado().getNombre() + " del Distrito de " +transferencia.getJuzgado().getSede().getDistrito().getNombre(),
                transferencia.getFechaTransferencia(),
                transferencia.getTotalExpediente()==null? 0: transferencia.getTotalExpediente(),
                transferidos,
                Objects.toString(transferencia.getUuid(),""),
                transferencia.getEstatus().name(),
                Objects.toString(transferencia.getObservaciones(),"")
        );
    }

}
