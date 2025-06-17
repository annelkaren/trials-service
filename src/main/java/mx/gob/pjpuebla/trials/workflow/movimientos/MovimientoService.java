package mx.gob.pjpuebla.trials.workflow.movimientos;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.oficialias.OficialiaRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.litigante.responselitigante.HistorialRecord;
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

    private final OficialiaRepository oficialiaRepository;

    private final MovimientoRepository movimientoRepository;
    private final CarpetaRepository carpetaRepository;
    private final PersonaService personaService;

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

    private String getCargo(String userId){
        String cargo = "-";
        List<RoleRecord> roles = personaService.getRolesByUser(userId);
        if (!roles.isEmpty() && roles.size() == 1) {
            cargo = roles.get(0).name();
        }
        return cargo;
    }

    private Movimiento createMovimiento(Carpeta carpeta, Documento documento, Persona persona, String motivo,
                                        String estado) {
        Oficialia oficialia = persona.getOficialia();

        if (estado.equals(EstadoCarpeta.DEVUELTO_A_OFICIALIA.name()) && persona.getJuzgado() != null) {
            oficialia = oficialiaRepository.findByJuzgadoId(persona.getJuzgado().getId())
                    .orElse(null);
        }

        return new Movimiento()
                .setCarpeta(carpeta)
                .setDocumento(documento)
                .setFechaAsignacion(LocalDateTime.now())
                .setMotivo(motivo)
                .setPersona(persona)
                .setEstado(estado)
                .setOficialia(oficialia)
                .setCargo(getCargo(persona.getUsuario()))
                .setJuzgado(persona.getJuzgado());
    }

    public Page<Movimiento> getAllBandejaRecepcion(Pageable pageable, Integer juzgadoId, List<EstadoCarpeta> estado,
                                                   String key, List<String> motivos, Persona personaId, TipoCarpeta tipoCarpetaNombre,
                                                   TipoDocumento tipoDocumentoNombre, Integer folio, TipoDocumento tipoEntradaDoc, TipoCarpeta tipoEntradaCarp) {
        return movimientoRepository.getAllBandejaRecepcion(pageable, juzgadoId, estado, key, motivos, personaId,
                tipoCarpetaNombre, tipoDocumentoNombre, folio, tipoEntradaDoc, tipoEntradaCarp);
    }

    public Page<Movimiento> getBandejaRecepcion(Pageable pageable, Integer juzgadoId, EstadoCarpeta estado, String key,
                                                String motivos, Persona personaId, TipoCarpeta tipoCarpetaNombre, TipoDocumento tipoDocumentoNombre,
                                                Integer folio, TipoDocumento tipoEntradaDoc, TipoCarpeta tipoEntradaCarp) {
        return movimientoRepository.getBandejaRecepcion(pageable, juzgadoId, estado, key, motivos, personaId,
                tipoCarpetaNombre, tipoDocumentoNombre, folio, tipoEntradaDoc, tipoEntradaCarp);
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
                                                 String key, TipoCarpeta tipoCarpeta, TipoDocumento tipoDocumento, Integer folio, TipoDocumento tipoEntradaDoc, TipoCarpeta tipoEntradaCarp) {
        return movimientoRepository.getAllBandejaEntrada(juzgadoId, oficialiaId, key, pageable, tipoCarpeta,
                tipoDocumento, folio, tipoEntradaDoc, tipoEntradaCarp);
    }

    public Map<String, Object> getOrigen(Integer documentoId, Integer carpetaId) {
        Movimiento movimiento;
        Persona persona;
        Map<String, Object> origen = new HashMap<>();

        if (documentoId != null) {
            movimiento = movimientoRepository.findFirstByDocumentoIdOrderByIdDesc(documentoId);
            if (movimiento != null) {
                persona = movimiento.getPersona();
                origen.put("nombrePersona", persona.getNombre() + " " + persona.getApellidoPaterno()
                        + (persona.getApellidoMaterno() != null ? " " + persona.getApellidoMaterno() : ""));
                origen.put("centroTrabajo", (movimiento.getOficialia() != null) ? movimiento.getOficialia().getNombre()
                        : movimiento.getJuzgado().getNombre());
                return origen;
            }

        }
        if (carpetaId != null) {
            movimiento = movimientoRepository.findFirstByCarpetaIdOrderByIdDesc(carpetaId);
            if (movimiento != null) {
                persona = movimiento.getPersona();
                origen.put("nombrePersona", persona.getNombre() + " " + persona.getApellidoPaterno()
                        + (persona.getApellidoMaterno() != null ? " " + persona.getApellidoMaterno() : ""));
                origen.put("centroTrabajo", (movimiento.getOficialia() != null) ?
                        movimiento.getOficialia().getNombre()
                        : movimiento.getJuzgado().getNombre());
                return origen;
            }
        }
        return origen;
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

    public List<HistorialRecord> getHistorialByExpediente(Integer carpetaId) {
        List<Movimiento> list = movimientoRepository.findByCarpetaIdOrderByFechaAsignacionDesc(carpetaId);
        return list.stream()
                .map(mov -> new HistorialRecord(
                        mov.getCargo(),
                        mov.getPersona().getNombre() + " " + mov.getPersona().getApellidoPaterno() + " "
                                + (mov.getPersona().getApellidoMaterno() != null ? mov.getPersona().getApellidoMaterno() : ""),
                        mov.getFechaAsignacion().toLocalDate(),
                        mov.getFechaAsignacion(),
                        mov.getEstado(),
                        mov.getConcepto()

                )).toList();
    }
}
