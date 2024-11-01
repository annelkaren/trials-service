package mx.gob.pjpuebla.trials.workflow.movimientos;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

import java.util.List;
import java.util.UUID;

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

    public Movimiento createMovimento(Carpeta carpeta, Documento documento, Persona persona, String motivo) {
        Movimiento movimiento = new Movimiento()
                .setCarpeta(carpeta)
                .setDocumento(documento)
                .setFechaAsignacion(LocalDateTime.now())
                .setMotivo(motivo)
                .setPersona(persona)
                .setOficialia(persona.getOficialia())
                .setJuzgado(persona.getJuzgado());
        movimiento = this.movimientoRepository.save(movimiento);
        return movimiento;
    }

    public Movimiento createMovimentoWithObservaciones(Carpeta carpeta, Documento documento, String estadoCarpeta, String observaciones, String recomendaciones) {
        Persona personaAuditor = personaService.getAuditor();
        Movimiento movimiento = new Movimiento()
                .setFechaAsignacion(LocalDateTime.now())
                .setMotivo(estadoCarpeta)
                .setPersona(personaAuditor)
                .setOficialia((personaAuditor.getOficialia() != null) ? personaAuditor.getOficialia() : null )
                .setJuzgado((personaAuditor.getJuzgado() != null) ? personaAuditor.getJuzgado() : null )
                .setCarpeta(carpeta)
                .setDocumento(documento)
                .setObservaciones(observaciones)
                .setRecomendaciones(recomendaciones);
        movimiento = movimientoRepository.save(movimiento);
        return movimiento;
    }

    public Page<Movimiento> getAllBandejaRecepcion(Pageable pageable, Integer juzgadoId, List<EstadoCarpeta> estado, String key, List<String> motivos) {
        return movimientoRepository.getAllBandejaRecepcion(pageable, juzgadoId, estado, key, motivos);
    }

    public void createMotivo(MotivoRecord motivoRecord) {
        Persona currentUser = personaService.getAuditor();
        Documento documento = documentoRepository.findById(motivoRecord.documentoId())
                .orElse(null);

        if (documento != null) {
            if (documento.getTipoDocumento() == TipoDocumento.PROMOCION) {
                createMovimento(null, documento, currentUser, "DEVUELTO - " + motivoRecord.motivo());
                documentoRepository.actualizarEstatus(documento.getId(), EstadoCarpeta.DEVUELTO);
            } else {
                createMovimento(documento.getCarpeta(), null, currentUser, "DEVUELTO - " + motivoRecord.motivo());
                carpetaRepository.actualizarEstatus(documento.getCarpeta().getId(), EstadoCarpeta.DEVUELTO);
            }
        }
    } 
}
