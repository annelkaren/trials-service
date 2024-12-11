package mx.gob.pjpuebla.trials.workflow.movimientos;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

import java.util.*;

import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional
@RequiredArgsConstructor
@Service
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CarpetaRepository carpetaRepository;
    private final DocumentoRepository documentoRepository;
    private final PersonaService personaService;

    public List<MovimientoSalidaRecord> getMovimientosSalida(String uuid) {
        UUID uuidMov = UUID.fromString(uuid);
        return movimientoRepository.getSalidas(uuidMov, EstadoCarpeta.TURNADO);
    }

    public Movimiento createMovimento(Carpeta carpeta, Documento documento, Persona persona, String motivo, String estado) {
        Movimiento movimiento = createMovimiento(carpeta, documento, persona, motivo, estado);
        movimiento = this.movimientoRepository.save(movimiento);
        return movimiento;
    }

    public Movimiento createMovimentoWithObservaciones(Carpeta carpeta, Documento documento, String estado, String observaciones, String recomendaciones, String motivo, String concepto, String duracion) {
        Persona personaAuditor = personaService.getAuditor();
        Movimiento movimiento = createMovimiento(carpeta, documento, personaAuditor, motivo, estado)
                .setObservaciones(observaciones)
                .setRecomendaciones(recomendaciones)
                .setConcepto(concepto)
                .setDuracion(duracion);

        movimiento = movimientoRepository.save(movimiento);
        return movimiento;
    }

    public Movimiento createMovimentoTurnado(Carpeta carpeta, Documento documento, Persona persona, String motivo, String estado, String concepto, Persona destino, String duracion) {
        Movimiento movimiento = createMovimiento(carpeta, documento, persona, motivo, estado)
                .setConcepto(concepto)
                .setDestino(destino)
                .setDuracion(duracion);
        movimiento = movimientoRepository.save(movimiento);
        return movimiento;
    }

    private Movimiento createMovimiento(Carpeta carpeta, Documento documento, Persona persona, String motivo, String estado) {
        return new Movimiento()
                .setCarpeta(carpeta)
                .setDocumento(documento)
                .setFechaAsignacion(LocalDateTime.now())
                .setMotivo(motivo)
                .setPersona(persona)
                .setEstado(estado)
                .setOficialia(persona.getOficialia())
                .setJuzgado(persona.getJuzgado());
    }

    public Page<Movimiento> getAllBandejaRecepcion(Pageable pageable, Integer juzgadoId, List<EstadoCarpeta> estado, String key, List<String> motivos) {
        return movimientoRepository.getAllBandejaRecepcion(pageable, juzgadoId, estado, key, motivos);
    }

    public Page<Movimiento> getBandejaRecepcion(Pageable pageable, Integer juzgadoId, EstadoCarpeta estado, String key, String motivos, Persona personaId) {
        return movimientoRepository.getBandejaRecepcion(pageable, juzgadoId, estado, key, motivos, personaId);
    }

    public void createMotivo(MotivoRecord motivoRecord) {
        Persona currentUser = personaService.getAuditor();
        Documento documento = documentoRepository.findById(motivoRecord.documentoId())
                .orElse(null);

        if (documento != null) {
            if (documento.getTipoDocumento() == TipoDocumento.PROMOCION) {
                createMovimento(null, documento, currentUser, motivoRecord.motivo(), EstadoCarpeta.DEVUELTO.name());
                documentoRepository.actualizarEstatus(documento.getId(), EstadoCarpeta.DEVUELTO);
            } else {
                createMovimento(documento.getCarpeta(), null, currentUser, motivoRecord.motivo(), EstadoCarpeta.DEVUELTO.name());
                carpetaRepository.actualizarEstatus(documento.getCarpeta().getId(), EstadoCarpeta.DEVUELTO);
            }
        }
    }

    public Page<Movimiento> getAllBandejaEntrada(Pageable pageable, Integer juzgadoId, Integer oficialiaId, String key) {
        return movimientoRepository.getAllBandejaEntrada(juzgadoId, oficialiaId, key, pageable);
    }

    public String getOrigen(Integer documentoId, Integer carpetaId) {
        Movimiento movimiento;
        if (documentoId != null) {
            movimiento = movimientoRepository.findFirstByDocumentoIdOrderByIdAsc(documentoId);
            return (movimiento.getOficialia() != null) ? movimiento.getOficialia().getNombre() : movimiento.getJuzgado().getNombre();
        }
        if (carpetaId != null) {
            movimiento = movimientoRepository.findFirstByCarpetaIdOrderByIdAsc(carpetaId);
            return (movimiento.getOficialia() != null) ? movimiento.getOficialia().getNombre() : movimiento.getJuzgado().getNombre();
        }
        return "";
    }

    public List<TurnadoMovimientoRecord> getTurnadoMovimientos(Integer carpetaId) {
        List<TurnadoMovimientoRecord> movimientoRecords = new ArrayList<>();
        List<Movimiento> list = movimientoRepository.findByCarpetaIdAndEstadoInOrderByIdAsc(carpetaId,
                Arrays.asList(EstadoCarpeta.TURNADO.name(), EstadoCarpeta.ASIGNADO.name(), EstadoCarpeta.CAPTURA.name()));
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getEstado().equals(EstadoCarpeta.TURNADO.name()) &&
                    (i+1)<list.size() && list.get(i + 1).getEstado().equals(EstadoCarpeta.ASIGNADO.name())) {
                Movimiento origen = list.get(i);
                Movimiento destino = list.get(i+1);
                TurnadoMovimientoRecord record = new TurnadoMovimientoRecord(
                        (origen.getUuid() != null) ? list.get(0).getOficialia().getNombre() : origen.getPersona().getNombre() + " " + origen.getPersona().getApellidoPaterno(),
                        destino.getPersona().getNombre() + " " + destino.getPersona().getApellidoPaterno(),
                        origen.getFechaAsignacion().toLocalDate(),
                        destino.getFechaAsignacion().toLocalDate(),
                        origen.getConcepto(),
                        destino.getConcepto(),
                        (origen.getDuracion().endsWith("h"))? origen.getDuracion().replace("h", " horas"):origen.getDuracion().replace("d","")
                );
                movimientoRecords.add(record);
            }
        }
        Collections.reverse(movimientoRecords);
        return movimientoRecords;
    }
}
