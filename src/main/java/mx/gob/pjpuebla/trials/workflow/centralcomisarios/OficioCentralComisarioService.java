package mx.gob.pjpuebla.trials.workflow.centralcomisarios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.EstadoCentralComisario;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.OficioResponseRecord;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;

@Service
@AllArgsConstructor
public class OficioCentralComisarioService {
    private final DocumentoRepository documentoRepository;
    private final OficioCentralComisarioRepository oficioCentralComisarioRepository;
    private final JuzgadoService juzgadoService;
    private final PersonaService personaService;
    private final MovimientoService movimientoService;

    public List<OficioResponseRecord> getOficiosCentralComisarios(String estadoOficio, Integer centralComisariosId) {
        EstadoCentralComisario estado = estadoOficio.isEmpty() ? null : EstadoCentralComisario.valueOf(estadoOficio.toUpperCase());

        return documentoRepository.findOficiosCentralComisarios(estado, centralComisariosId);
    }

    public OficioResponseRecord registraOficioCentralComisarios(Integer oficioId) {
        Documento documento = documentoRepository.findById(oficioId).orElseThrow(() -> new NotFoundException("Oficio no encontrado con ID: " + oficioId));
        Persona personaEnvia = personaService.getAuditor();
        Juzgado centralComisario = juzgadoService.getCentralComisarios(personaEnvia.getJuzgado());

        OficioCentralComisario oficioCentralComisario = new OficioCentralComisario();

        oficioCentralComisario.setDocumento(documento);
        oficioCentralComisario.setCentralComisarios(centralComisario);
        oficioCentralComisario.setPersonaEnvia(personaEnvia);
        oficioCentralComisario.setFechaEnvio(LocalDateTime.now());
        oficioCentralComisario.setEstado(EstadoCentralComisario.PENDIENTE);
        oficioCentralComisarioRepository.save(oficioCentralComisario);

        documento.setEstatus(EstadoCarpeta.CENTRAL_COMISARIOS);
        documento.getCarpeta().setEstatus(EstadoCarpeta.CENTRAL_COMISARIOS);
        documentoRepository.save(documento);
        return new OficioResponseRecord(documento.getId(), documento.getFolio(), documento.getInstitucion().getNombre(), "", documento.getEstatus(), documento.getEstatus().getEtiqueta(), null, null, null, null, null, documento.getCarpeta().getExpediente());
    }

    public void actualizaOficioCentralComisarios(Integer oficioId, Integer personaId, EstadoCentralComisario nuevoEstado) {
        OficioCentralComisario oficioCentralComisario = oficioCentralComisarioRepository.findByDocumentoId(oficioId)
                .orElseThrow(() -> new NotFoundException("Oficio no encontrado con ID: " + oficioId));

        Documento documento = documentoRepository.findById(oficioId).orElseThrow(() -> new NotFoundException("Documento no encontrado con ID: " + oficioId));

        Persona personaAsignada = personaService.findPersonaById(personaId.longValue()).orElseThrow(()-> new NotFoundException("Persona no existe"));

        switch (nuevoEstado) {
            case RECIBIDO: {
                oficioCentralComisario.setFechaRecepcion(LocalDateTime.now());
                oficioCentralComisario.setPersonaRecibe(personaAsignada);
                movimientoService.createMovimentoTurnado(documento.getCarpeta(), null, documento.getCarpeta().getPersona(), nuevoEstado.getEtiqueta(), EstadoCarpeta.CENTRAL_COMISARIOS.getEtiqueta(), "OFICIOS", personaAsignada, "3");
                break;
            }
            case ASIGNADO: {
                oficioCentralComisario.setComisario(personaAsignada);
                break;
            }
            case NOTIFICADO: {
                oficioCentralComisario.setFechaNotificacion(LocalDateTime.now());
                break;
            }
            case DEVUELTO: {
                oficioCentralComisario.setFechaDevolucion(LocalDateTime.now());
                oficioCentralComisario.setPersonaDevuelve(personaAsignada);
                break;
            }
            default:
                break;
        }

        oficioCentralComisario.setEstado(nuevoEstado);
        oficioCentralComisarioRepository.save(oficioCentralComisario);
    }
}