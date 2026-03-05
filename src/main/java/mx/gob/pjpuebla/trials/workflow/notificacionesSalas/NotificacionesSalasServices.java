package mx.gob.pjpuebla.trials.workflow.notificacionesSalas;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.apis.sendPulse.EmailGatewayService;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.acuseNotificacion.AcuseNotificacionService;
import mx.gob.pjpuebla.trials.workflow.documentos.DigitalizacionService;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DigitalizacionRecord;
import mx.gob.pjpuebla.trials.workflow.emailLogs.EmailLogs;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaCreateRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaCreateResponseRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaDestinatarioCreateRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaDestinatarioRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaDetalleRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionesSalasRecord;
import net.sf.jasperreports.engine.JRException;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificacionesSalasServices {

  
    private final NotificacionesSalasRepository notificacionesSalasRepository;
    private final NotificacionSalaDestinatarioRepository notificacionSalaDestinatarioRepository;
    private final DigitalizacionService digitalizacionService;
    private final EmailGatewayService emailGatewayService;
    private final AcuseNotificacionService acuseNotificacionService;
    @Value("${app.public-api-base-url}")
    private String publicApiBaseUrl;

    public Page<NotificacionesSalasRecord> getPageNotificaciones(Pageable pageable, String q, String numeroExpediente,
            String nombreDestinatario, String correoElectronico, LocalDateTime fechaEnvioFrom,
            LocalDateTime fechaEnvioTo, LocalDateTime fechaTerminoFrom, LocalDateTime fechaTerminoTo,
            String tipoSala) {


        return notificacionesSalasRepository.findPageNotificaciones(pageable);
    }

    @Transactional
    public NotificacionSalaCreateResponseRecord createNotificacion(NotificacionSalaCreateRecord request,
            MultipartFile archivo) {

        validaciones(request, archivo);

        String toca = request.toca().trim();
        String tipoSala = request.tipoSala().trim();
        String nombreSala = request.nombreSala().trim();
        String contenidoCorreo = request.contenidoCorreo().trim();

        NotificacionesSalas notificacion = new NotificacionesSalas()
                .setToca(toca)
                .setTipoSala(tipoSala)
                .setNombreSala(nombreSala)
                .setFechaTermino(request.fechaTermino() != null ? request.fechaTermino().atStartOfDay() : null)
                .setContenidoCorreo(contenidoCorreo)
                .setFechaEnvio(LocalDateTime.now())
                .setEstado(Estado.ACTIVE);

        notificacion = notificacionesSalasRepository.save(notificacion);

        DigitalizacionRecord digitalizacionRecord = digitalizacionService.guardarArchivoNotificacionSala(archivo, nombreSala,
                notificacion.getId());

        notificacion.setRutaArchivo(digitalizacionRecord.nombreArchivo());
        notificacion = notificacionesSalasRepository.save(notificacion);
        String publicDownloadUrl = buildPublicDownloadUrl(digitalizacionRecord.nombreArchivo());
        String htmlFinalCorreo = appendDownloadLink(contenidoCorreo, publicDownloadUrl);

        int exitosos = 0;
        int fallidos = 0;
        for (NotificacionSalaDestinatarioCreateRecord destinatarioRequest : request.destinatarios()) {
            NotificacionSalaDestinatario destinatario = new NotificacionSalaDestinatario();
            destinatario.setNotificacionSala(notificacion);
            destinatario.setNombreDestinatario(destinatarioRequest.nombreDestinatario().trim());
            destinatario.setCorreoElectronico(destinatarioRequest.correoElectronico().trim());
            destinatario.setTipoParte(destinatarioRequest.tipoParte().trim());
            destinatario.setEstado(Estado.ACTIVE);

            try {
                EmailLogs emailLog = enviarCorreoNotificacion(
                        destinatarioRequest.nombreDestinatario().trim(),
                        htmlFinalCorreo,
                        destinatarioRequest.correoElectronico().trim());
                destinatario.setEmailLog(emailLog);
                exitosos++;
            } catch (Exception ex) {
                log.warn("No se pudo enviar correo para destinatario {} en notificacionSala {}: {}",
                        destinatarioRequest.correoElectronico(),
                        notificacion.getId(),
                        ex.getMessage());
                fallidos++;
            }

            notificacionSalaDestinatarioRepository.save(destinatario);
        }

        return new NotificacionSalaCreateResponseRecord(
                notificacion.getId(),
                notificacion.getToca(),
                notificacion.getNombreSala(),
                notificacion.getTipoSala(),
                notificacion.getFechaEnvio(),
                notificacion.getFechaTermino(),
                digitalizacionRecord.nombreArchivo(),
                request.destinatarios().size(),
                exitosos,
                fallidos);
    }

    @Transactional(readOnly = true)
    public NotificacionSalaDetalleRecord getDetalle(Integer idNotificacionSala) {

        NotificacionesSalas notificacion = notificacionesSalasRepository.findById(idNotificacionSala)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificacion no encontrada."));

        List<NotificacionSalaDestinatarioRecord> destinatarios = notificacionSalaDestinatarioRepository
            .findSalaDestinatarioRecord(idNotificacionSala);

        return new NotificacionSalaDetalleRecord(
                notificacion.getId(),
                notificacion.getToca(),
                notificacion.getNombreSala(),
                notificacion.getTipoSala(),
                notificacion.getFechaEnvio(),
                notificacion.getFechaTermino(),
                notificacion.getContenidoCorreo(),
                notificacion.getRutaArchivo(),
                extractFileName(notificacion.getRutaArchivo()),
                destinatarios);
    }

    private EmailLogs enviarCorreoNotificacion(String nombreDestinatario, String contenidoCorreoHtml, String correoElectronico) {

        return emailGatewayService.sendAndLog(
                correoElectronico,
                nombreDestinatario,
                "Notificacion de sala",
                contenidoCorreoHtml,
                null,
                null);
    }

    public byte[] downloadArchivo(Integer idNotificacionSala) throws java.io.IOException {
        
        NotificacionesSalas notificacion = notificacionesSalasRepository.findById(idNotificacionSala)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificacion no encontrada."));

        if (notificacion.getRutaArchivo() == null || notificacion.getRutaArchivo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La notificacion no tiene archivo.");
        }

        String rutaArchivo =  "notificacionesSalas/" + notificacion.getNombreSala().replace(" ", "") + "/" + notificacion.getRutaArchivo();

        return digitalizacionService.getArchivoNotificacionSala(rutaArchivo);
    }

    public byte[] downloadArchivoPublico(String nombreArchivo) {
        validarNombreArchivo(nombreArchivo);
        
        NotificacionesSalas notificacion = notificacionesSalasRepository.findByRutaArchivo(nombreArchivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo no encontrado repositorio."));
        
        String rutaArchivo =  "notificacionesSalas/" + notificacion.getNombreSala().replace(" ", "") + "/" + notificacion.getRutaArchivo();

        if (rutaArchivo == null || rutaArchivo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo no encontrado.");
        }

        try {
            return digitalizacionService.getArchivoNotificacionSala(rutaArchivo);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo no encontrado.");
        }
    }

    public byte[] getAcuseNotificacion(Integer notificacionSalaDestinatarioId) throws JRException, IOException  {
        return acuseNotificacionService.getAcuseNotificacionService(notificacionSalaDestinatarioId);
    }

    private void validaciones(NotificacionSalaCreateRecord request, MultipartFile archivo) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La informacion de la notificacion es obligatoria.");
        }

        if (request.toca() == null || request.toca().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El numero de expediente es obligatorio.");
        }
        if (request.nombreSala() == null || request.nombreSala().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de sala es obligatorio.");
        }
        if (request.tipoSala() == null || request.tipoSala().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de sala es obligatorio.");
        }
        if (request.contenidoCorreo() == null || request.contenidoCorreo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El contenido del correo es obligatorio.");
        }
        if (request.destinatarios() == null || request.destinatarios().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe registrar al menos un destinatario.");
        }

        for (int i = 0; i < request.destinatarios().size(); i++) {
            NotificacionSalaDestinatarioCreateRecord destinatario = request.destinatarios().get(i);
            int index = i + 1;
            if (destinatario == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La informacion del destinatario " + index + " es obligatoria.");
            }
            if (destinatario.nombreDestinatario() == null || destinatario.nombreDestinatario().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El nombre del destinatario " + index + " es obligatorio.");
            }
            if (destinatario.correoElectronico() == null || destinatario.correoElectronico().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El correo del destinatario " + index + " es obligatorio.");
            }
          
            if (destinatario.tipoParte() == null || destinatario.tipoParte().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El tipo de parte del destinatario " + index + " es obligatorio.");
            }
        }

        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo es obligatorio.");
        }
    }

    private String sanitizeFilename(String name) {
        if (name == null || name.isBlank()) {
            return "adjunto";
        }
        return name.replaceAll("[\\\\/]+", "_");
    }

    private String extractFileName(String rutaArchivo) {
        if (rutaArchivo == null || rutaArchivo.isBlank()) {
            return null;
        }
        String normalized = rutaArchivo.replace("\\", "/");
        int idx = normalized.lastIndexOf('/');
        return idx >= 0 ? normalized.substring(idx + 1) : normalized;
    }

    private String buildPublicDownloadUrl(String nombreArchivo) {
        String encodedNombreArchivo = UriUtils.encodePathSegment(nombreArchivo, StandardCharsets.UTF_8);
        String baseUrl = publicApiBaseUrl != null ? publicApiBaseUrl.trim() : "";
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl + "/api/workflow/notificacionesSalas/download/" + encodedNombreArchivo;
    }

    private String appendDownloadLink(String contenidoCorreoHtml, String downloadUrl) {
        StringBuilder html = new StringBuilder(contenidoCorreoHtml == null ? "" : contenidoCorreoHtml);
        html.append("<hr/>")
                .append("<p><strong>Descargar documento:</strong> ")
                .append("<a href=\"")
                .append(downloadUrl)
                .append("\">")
                .append(downloadUrl)
                .append("</a></p>");
        return html.toString();
    }

    private void validarNombreArchivo(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre de archivo invalido.");
        }
        if (nombreArchivo.contains("/") || nombreArchivo.contains("\\") || nombreArchivo.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre de archivo invalido.");
        }
    }
}
