package mx.gob.pjpuebla.trials.workflow.movimientos;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;

import java.util.*;

import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
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
    private final PersonaService personaService;
    private final RoleService roleService;

    public List<MovimientoSalidaRecord> getMovimientosSalida(String uuid) {
        UUID uuidMov = UUID.fromString(uuid);
        return movimientoRepository.getSalidas(uuidMov, EstadoCarpeta.RECEPCION);
    }

    public Movimiento createMovimento(Carpeta carpeta, Documento documento, Persona persona, String motivo,
            String estado) {
        Movimiento movimiento = createMovimiento(carpeta, documento, persona, motivo, estado);
        movimiento = this.movimientoRepository.save(movimiento);
        return movimiento;
    }

    public Movimiento createMovimentoWithObservaciones(Carpeta carpeta, Documento documento, String estado,
            String observaciones, String recomendaciones, String motivo, String concepto, String duracion) {
        Persona personaAuditor = personaService.getAuditor();
        Movimiento movimiento = createMovimiento(carpeta, documento, personaAuditor, motivo, estado)
                .setObservaciones(observaciones)
                .setRecomendaciones(recomendaciones)
                .setConcepto(concepto)
                .setDuracion(duracion);

        movimiento = movimientoRepository.save(movimiento);
        return movimiento;
    }

    public Movimiento createMovimentoTurnado(Carpeta carpeta, Documento documento, Persona persona, String motivo,
            String estado, String concepto, Persona destino, String duracion) {
        Movimiento movimiento = createMovimiento(carpeta, documento, persona, motivo, estado)
                .setConcepto(concepto)
                .setDestino(destino)
                .setDuracion(duracion);
        movimiento = movimientoRepository.save(movimiento);
        return movimiento;
    }

    public Movimiento createMovimentoWithConcepto(Carpeta carpeta, Documento documento, Persona persona, String motivo,
            String estado, Concepto concepto) {
        Movimiento movimiento = createMovimiento(carpeta, documento, persona, motivo, estado);
        movimiento.setConcepto((concepto != null) ? concepto.getNombre() : null);
        movimiento.setDuracion((concepto != null) ? concepto.getDias().toString() + "d" : null);
        movimiento = this.movimientoRepository.save(movimiento);
        return movimiento;
    }

    public Movimiento createMovimentoPromocionElectronica(Documento documento, Persona persona, String estado,
            Concepto concepto) {
        Movimiento movimiento = createMovimiento(null, documento, persona, "", estado);
        movimiento.setConcepto(concepto.getNombre());
        movimiento.setDuracion(concepto.getDias().toString() + "d");
        movimiento.setJuzgado(documento.getCarpeta().getJuzgado());
        movimiento = this.movimientoRepository.save(movimiento);
        return movimiento;
    }

    private Movimiento createMovimiento(Carpeta carpeta, Documento documento, Persona persona, String motivo,
            String estado) {
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

    public Page<Movimiento> getAllBandejaRecepcion(Pageable pageable, Integer juzgadoId, List<EstadoCarpeta> estado,
            String key, List<String> motivos, Persona personaId) {
        return movimientoRepository.getAllBandejaRecepcion(pageable, juzgadoId, estado, key, motivos, personaId);
    }

    public Page<Movimiento> getBandejaRecepcion(Pageable pageable, Integer juzgadoId, EstadoCarpeta estado, String key,
            String motivos, Persona personaId) {
        return movimientoRepository.getBandejaRecepcion(pageable, juzgadoId, estado, key, motivos, personaId);
    }

    public void createMotivo(MotivoRecord motivoRecord) {
        Persona currentUser = personaService.getAuditor();
        Carpeta carpeta = carpetaRepository.findById(motivoRecord.documentoId())
                .orElseThrow(() -> new NotFoundException("Carpeta no encontrada",
                        "carpetaId: " + motivoRecord.documentoId()));

       
        if (motivoRecord.devueltoOficialia()) {
           
            createMovimento(carpeta, null, currentUser, motivoRecord.motivo(),
                    EstadoCarpeta.DEVUELTO_A_OFICIALIA.name());
            carpetaRepository.actualizarEstatus(carpeta.getId(),
                    EstadoCarpeta.DEVUELTO_A_OFICIALIA);
           
        } else {
           
            createMovimento(carpeta, null, currentUser, motivoRecord.motivo(),
                    EstadoCarpeta.DEVUELTO.name());
            carpetaRepository.actualizarEstatus(carpeta.getId(), EstadoCarpeta.DEVUELTO);
           
        }


    }

    public Page<Movimiento> getAllBandejaEntrada(Pageable pageable, Integer juzgadoId, Integer oficialiaId,
            String key, TipoCarpeta tipoCarpeta, TipoDocumento tipoDocumento, Integer folio) {
        return movimientoRepository.getAllBandejaEntrada(juzgadoId, oficialiaId, key, pageable, tipoCarpeta, tipoDocumento, folio);
    }

    public String getOrigen(Integer documentoId, Integer carpetaId) {
        Movimiento movimiento;
        if (documentoId != null) {
            movimiento = movimientoRepository.findFirstByDocumentoIdOrderByIdAsc(documentoId);
            return (movimiento.getOficialia() != null) ? movimiento.getOficialia().getNombre()
                    : movimiento.getJuzgado().getNombre();
        }
        if (carpetaId != null) {
            movimiento = movimientoRepository.findFirstByCarpetaIdOrderByIdAsc(carpetaId);
            return (movimiento.getOficialia() != null) ? movimiento.getOficialia().getNombre()
                    : movimiento.getJuzgado().getNombre();
        }
        return "";
    }

    public List<TurnadoMovimientoRecord> getTurnadoMovimientos(Integer carpetaId) {
        List<TurnadoMovimientoRecord> movimientoRecords = new ArrayList<>();
        List<Movimiento> list = movimientoRepository.findByCarpetaIdAndEstadoInOrderByIdAsc(carpetaId,
                Arrays.asList(EstadoCarpeta.TURNADO.name(), EstadoCarpeta.ASIGNADO.name(),
                        EstadoCarpeta.CAPTURA.name()));
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getEstado().equals(EstadoCarpeta.TURNADO.name()) &&
                    (i + 1) < list.size() && list.get(i + 1).getEstado().equals(EstadoCarpeta.ASIGNADO.name())) {
                TurnadoMovimientoRecord turnadoMovimientoRecord = createTurnadoMovimientoRecord(list.get(i),
                        list.get(i + 1), list);
                movimientoRecords.add(turnadoMovimientoRecord);
            }
        }
        Collections.reverse(movimientoRecords);
        return movimientoRecords;
    }

    /* SE CREA METODO PARA CORREGIR SCAN DE QODANA */
    public TurnadoMovimientoRecord createTurnadoMovimientoRecord(Movimiento origen, Movimiento destino,
            List<Movimiento> list) {
        return new TurnadoMovimientoRecord(
                (origen.getUuid() != null) ? list.get(0).getOficialia().getNombre()
                        : origen.getPersona().getNombre() + " " + origen.getPersona().getApellidoPaterno(),
                destino.getPersona().getNombre() + " " + destino.getPersona().getApellidoPaterno(),
                origen.getFechaAsignacion().toLocalDate(),
                destino.getFechaAsignacion().toLocalDate(),
                origen.getConcepto(),
                destino.getConcepto(),
                (origen.getDuracion().endsWith("h")) ? origen.getDuracion().replace("h", " horas")
                        : origen.getDuracion().replace("d", ""));
    }
}
