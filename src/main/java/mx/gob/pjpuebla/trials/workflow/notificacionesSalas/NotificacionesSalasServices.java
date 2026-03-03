package mx.gob.pjpuebla.trials.workflow.notificacionesSalas;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.config.sendPulse.EmailGatewayService;
import mx.gob.pjpuebla.trials.config.sendPulse.EmailLog;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.documentos.DigitalizacionService;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DigitalizacionRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaCreateRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaCreateResponseRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaDestinatarioCreateRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaDestinatarioRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaDetalleRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionesSalasRecord;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificacionesSalasServices {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final NotificacionesSalasRepository notificacionesSalasRepository;
    private final NotificacionSalaDestinatarioRepository notificacionSalaDestinatarioRepository;
    private final DigitalizacionService digitalizacionService;
    private final EmailGatewayService emailGatewayService;
    private final Configuration freemarkerConfig;

    public Page<NotificacionesSalasRecord> getPageNotificaciones(Pageable pageable, String q, String numeroExpediente,
            String nombreDestinatario, String correoElectronico, LocalDateTime fechaEnvioFrom,
            LocalDateTime fechaEnvioTo, LocalDateTime fechaTerminoFrom, LocalDateTime fechaTerminoTo,
            String tipoSala) {

        // TODO: Rehabilitar filtrado dinámico una vez estabilizada la consulta JPQL/SQL.
        return notificacionesSalasRepository.findPageNotificaciones(pageable);
    }

    @Transactional
    public NotificacionSalaCreateResponseRecord createNotificacion(NotificacionSalaCreateRecord request,
            MultipartFile archivo) {
        validaciones(request, archivo);

        String numeroExpediente = request.numeroExpediente().trim();
        String tipoSala = request.tipoSala().trim();
        String nombreSala = request.nombreSala().trim();

        NotificacionesSalas notificacion = new NotificacionesSalas()
                .setToca(numeroExpediente)
                .setTipoSala(tipoSala)
                .setNombreSala(nombreSala)
                .setFechaTermino(request.fechaTermino() != null ? request.fechaTermino().atStartOfDay() : null)
                .setFechaEnvio(LocalDateTime.now())
                .setEstado(Estado.ACTIVE);

        notificacion = notificacionesSalasRepository.save(notificacion);

        DigitalizacionRecord digitalizacionRecord = digitalizacionService.guardarArchivoNotificacionSala(archivo,
                notificacion.getId(), tipoSala);

        notificacion.setRutaArchivo(digitalizacionRecord.rutaArchivo());
        notificacion = notificacionesSalasRepository.save(notificacion);

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
                EmailLog emailLog = enviarCorreoNotificacion(
                        archivo,
                        destinatarioRequest.nombreDestinatario().trim(),
                        numeroExpediente,
                        nombreSala,
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
                .findByNotificacionSalaId(idNotificacionSala)
                .stream()
                .map(destinatario -> new NotificacionSalaDestinatarioRecord(
                        destinatario.getId(),
                        destinatario.getNombreDestinatario(),
                        destinatario.getCorreoElectronico(),
                        destinatario.getTipoParte(),
                        destinatario.getEstado(),
                        destinatario.getEmailLog() != null ? destinatario.getEmailLog().getEstado() : null,
                        destinatario.getEmailLog() != null ? destinatario.getEmailLog().getFechaEntrega() : null,
                        destinatario.getEmailLog() != null ? destinatario.getEmailLog().getFechaLectura() : null))
                .collect(Collectors.toList());

        int totalDestinatarios = destinatarios.size();
        int destinatariosExitosos = (int) destinatarios.stream().filter(d -> d.estadoEnvioCorreo() != null).count();
        int destinatariosFallidos = totalDestinatarios - destinatariosExitosos;

        return new NotificacionSalaDetalleRecord(
                notificacion.getId(),
                notificacion.getToca(),
                notificacion.getNombreSala(),
                notificacion.getTipoSala(),
                notificacion.getFechaEnvio(),
                notificacion.getFechaTermino(),
                notificacion.getRutaArchivo(),
                extractFileName(notificacion.getRutaArchivo()),
                totalDestinatarios,
                destinatariosExitosos,
                destinatariosFallidos,
                destinatarios);
    }

    private EmailLog enviarCorreoNotificacion(MultipartFile archivo, String nombreDestinatario, String toca,
            String nombreSala, String correoElectronico) {

        byte[] attachmentBytes;
        String attachmentName = sanitizeFilename(archivo.getOriginalFilename());
        String html;

        try {
            attachmentBytes = archivo.getBytes();
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("nombreParticipante", nombreDestinatario);
            parameters.put("toca", toca);
            parameters.put("nombreSala", nombreSala);

            Template template = freemarkerConfig.getTemplate("Notificaciones.ftl");
            html = FreeMarkerTemplateUtils.processTemplateIntoString(template, parameters);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el archivo adjunto", e);
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo generar el correo de notificacion", ex);
        }

        return emailGatewayService.sendAndLog(
                correoElectronico,
                nombreDestinatario,
                "Notificacion de sala",
                html,
                attachmentName,
                attachmentBytes);
    }

    public byte[] downloadArchivo(Integer idNotificacionSala) throws java.io.IOException {
        NotificacionesSalas notificacion = notificacionesSalasRepository.findById(idNotificacionSala)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificacion no encontrada."));

        if (notificacion.getRutaArchivo() == null || notificacion.getRutaArchivo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La notificacion no tiene archivo.");
        }
        return digitalizacionService.getArchivoNotificacionSala(notificacion.getRutaArchivo());
    }

    private void validaciones(NotificacionSalaCreateRecord request, MultipartFile archivo) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La informacion de la notificacion es obligatoria.");
        }

        if (request.numeroExpediente() == null || request.numeroExpediente().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El numero de expediente es obligatorio.");
        }
        if (request.nombreSala() == null || request.nombreSala().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de sala es obligatorio.");
        }
        if (request.tipoSala() == null || request.tipoSala().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de sala es obligatorio.");
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
            if (!EMAIL_PATTERN.matcher(destinatario.correoElectronico().trim()).matches()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El formato del correo del destinatario " + index + " es invalido.");
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

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
