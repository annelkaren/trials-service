package mx.gob.pjpuebla.trials.workflow.listaestrados;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.notificaciones.NotificacionRepository;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.ErrorRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.notificaciones.Notificacion;
import net.sf.jasperreports.engine.JRException;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListaEstradoService {

    private final ListaEstradoRepository listaEstradoRepository;
    private final NotificacionRepository notificacionRepository;
    private final PersonaService personaService;
    private final DocumentoDetalleRepository documentoDetalleRepository;
    private final ListaEstradoGenerator generator;

    @Transactional(readOnly = true)
    public Page<ListaEstradoRecord> findAllByListaEstradoId(Integer listaEstrado, String searchQuery,
            Pageable pageable) {

        Page<ListaEstrado> listaEstradoPage = listaEstradoRepository.findAllListaEstradoIdAndSearch(
                searchQuery == null ? "" : searchQuery,
                listaEstrado,
                pageable);

        return listaEstradoPage.map(le -> {
            // Inicializar la relación antes de serializar
            if (le.getPersona() != null) {
                // Forzamos la inicialización del domicilio si es necesario
                le.getPersona().getDomicilio(); // noinspection ResultOfMethodCallIgnored 
            }

            long noNotificaciones = notificacionRepository.countNotificacionesByListaEstradoId(le.getId());

            String nombreCompleto = (le.getPersona().getNombre() != null ? le.getPersona().getNombre() : "") +
                    (le.getPersona().getApellidoMaterno() != null ? " " + le.getPersona().getApellidoMaterno() : "") +
                    (le.getPersona().getApellidoPaterno() != null ? " " + le.getPersona().getApellidoPaterno() : "");

            return new ListaEstradoRecord(
                    le.getId(),
                    le.getFechaAlta().toString(),
                    (int) noNotificaciones,
                    nombreCompleto);
        });
    }

    public ResponseEntity<Object> getReporteListaEstrados(Integer id) {
        List<Notificacion> notificacionList = notificacionRepository.getNotificacionByTipo(id);
        if (notificacionList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND_404)
                    .body(new ErrorRecord("ListaEstrados", "No hay registros"));
        }

        Persona persona = personaService.getAuditor();
        String nombreCentroTrabajo;
        if (persona.getJuzgado() != null) {
            nombreCentroTrabajo = persona.getJuzgado().getNombre();
        } else {
            nombreCentroTrabajo = persona.getOficialia().getNombre();
        }

        List<ListaEstradoDTO> listaEstradosDTO = notificacionList.stream()
                .map(notificacion -> {
                    String juzgado = nombreCentroTrabajo != null ? nombreCentroTrabajo : "";
                    String diaPublicado = LocalDate.now().toString();
                    String asunto = "EXP." + notificacion.getDocumento().getCarpeta().getExpediente()
                            + "\n" + notificacion.getDocumento().getCarpeta().getTipoJuicio().getNombre()
                            + "\n" + "***** VS *****";

                    // validamos si es de tipo sentencia o acuerdo he imprimimos o rubros o extracto
                    // de sentencia
                    String nombresRubros = "";
                   
                    if (notificacion.getDocumento().getTipoDocumento().equals(TipoDocumento.SENTENCIA)) {
                       
                        DocumentoDetalle docDetalle = documentoDetalleRepository
                                .findByDocumentoId(notificacion.getDocumento().getId()).orElse(null);
                        if (docDetalle != null) {
                            nombresRubros = docDetalle.getExtractoSentencia();
                        }
                    } else {
                       
                        nombresRubros = String.join(", ", notificacion.getDocumento().getData().getRubros());


                    }

                    DocumentoDetalle doc = documentoDetalleRepository
                            .findByDocumentoId(notificacion.getDocumento().getId())
                            .orElseThrow(() -> new NotFoundException("Documento no encontrado",
                                    String.valueOf(notificacion.getDocumento().getId())));
                    String notificacionDetalle = "Auto de fecha "
                            + (doc.getFechaResolucion() != null ? doc.getFechaResolucion().toString() : "")
                            + "\n" + nombresRubros;
                    return new ListaEstradoDTO(juzgado, asunto, notificacionDetalle, diaPublicado);
                })
                .toList();

        // Armado de pdf
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("reporte", "notificaciones_" + UUID.randomUUID() + ".pdf");

            byte[] reporte = generator.getReporteListaEstrados(listaEstradosDTO);

            return ResponseEntity.ok().headers(headers).body(reporte);
        } catch (IOException | JRException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                    .body(new ErrorRecord("ListaSalida", e.getMessage()));
        }
    }
}
