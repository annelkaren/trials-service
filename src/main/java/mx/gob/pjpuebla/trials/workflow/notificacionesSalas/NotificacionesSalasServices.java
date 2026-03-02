package mx.gob.pjpuebla.trials.workflow.notificacionesSalas;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionesSalasRecord;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificacionesSalasServices {

    private final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final NotificacionesSalasRepository notificacionesSalasRepository;
    private final DigitalizacionService digitalizacionService;
    private final EmailGatewayService emailGatewayService;
    private final Configuration freemarkerConfig;


    public Page<NotificacionesSalasRecord> getPageNotificaciones(Pageable pageable, String key, String expediente,
            String destino, String correo, LocalDateTime fechaEnvioFrom, LocalDateTime fechaEnvioTo,
            LocalDateTime fechaTerminoFrom, LocalDateTime fechaTerminoTo) {

        return notificacionesSalasRepository.getPageNotificaciones(pageable);
    }

    @Transactional
    public NotificacionesSalasRecord createNotificacion(String toca, String tipoSala,
            String nombreDestinatario,
            String correoElectronico, LocalDate fechaTermino, MultipartFile archivo) {

        validaciones(toca, tipoSala, nombreDestinatario, correoElectronico, archivo);

        NotificacionesSalas notificacion = new NotificacionesSalas()
                .setToca(toca.trim())
                .setTipoSala(tipoSala.trim())
                .setNombreDestinatario(nombreDestinatario.trim())
                .setCorreoDestinatario(correoElectronico.trim())
                .setFechaTermino(fechaTermino != null ? fechaTermino.atStartOfDay() : null)
                .setFechaEnvio(LocalDateTime.now())
                .setEstado(Estado.ACTIVE);

        // Enviar correo:
        // Definimos variables para archivo:
        byte[] attachmentBytes = null;
        String attachmentName = null;

        if (archivo != null && !archivo.isEmpty()) {
            attachmentName = sanitizeFilename(archivo.getOriginalFilename());
            try {
                attachmentBytes = archivo.getBytes();
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo leer el archivo adjunto", e);
            }
        }
        
         Map<String, Object> parameters = new HashMap<>();
         parameters.put("nombreParticipante", nombreDestinatario);
         parameters.put("toca", toca);
         parameters.put("nombreSala", "SALA DE PRUEBA"); 
        
        String html = "";
        try {
              Template template = freemarkerConfig.getTemplate("Notificaciones.ftl");
        html = FreeMarkerTemplateUtils.processTemplateIntoString(template, parameters);

        } catch (Exception e) {
            log.error(e.getStackTrace().toString());
        }
      
       
        EmailLog log = emailGatewayService.sendAndLog(
                correoElectronico,
                nombreDestinatario,
                "Notificación",
                html,
                attachmentName, 
                attachmentBytes);

        notificacion.setEmailLog(log);

        notificacion = notificacionesSalasRepository.save(notificacion);

        DigitalizacionRecord digitalizacionRecord = digitalizacionService
                .guardarArchivoNotificacionSala(archivo, notificacion.getId(), tipoSala);

        notificacion.setRutaArchivo(digitalizacionRecord.nombreArchivo());

        notificacion = notificacionesSalasRepository.save(notificacion);

        // Aqui intentamos enviar el correo con SEND PULS:

        return new NotificacionesSalasRecord(
                notificacion.getId(),
                notificacion.getToca(),
                notificacion.getTipoSala(),
                notificacion.getNombreDestinatario(),
                notificacion.getCorreoDestinatario(),
                notificacion.getFechaTermino(),
                notificacion.getRutaArchivo(),
                notificacion.getRutaArchivo(),
                notificacion.getFechaEnvio(),
                null,null);
                //TODO: obtener fecha lectura y fecha entrega desde el log de correos.
                //notificacion.getFechaLectura(),
                //notificacion.getFechaEntrega());
    }

    public byte[] downloadArchivo(Integer idNotificacionSala) throws java.io.IOException {
        NotificacionesSalas notificacion = notificacionesSalasRepository.findById(idNotificacionSala)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificacion no encontrada."));

        if (notificacion.getRutaArchivo() == null || notificacion.getRutaArchivo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La notificacion no tiene archivo.");
        }
        return digitalizacionService.getArchivoNotificacionSala(notificacion.getRutaArchivo(),
                notificacion.getTipoSala());
    }

    /**
     * Realiza validaciones de los campos obligatorios para la notificacion de sala.
     * 
     * @param numeroExpediente   el numero de expediente de la notificacion de sala.
     * @param tipoSala           el tipo de sala (ENTREGADO, SALA, etc.).
     * @param nombreDestinatario el nombre del destinatario de la notificacion de
     *                           sala.
     * @param correoElectronico  el correo electronico del destinatario de la
     *                           notificacion de sala.
     * @param archivo            el archivo adjunto a la notificacion de sala.
     * 
     * @throws ResponseStatusException si alguno de los campos obligatorios no se
     *                                 cumplen.
     */
    private void validaciones(String numeroExpediente, String tipoSala, String nombreDestinatario,
            String correoElectronico, MultipartFile archivo) {
         
        if (numeroExpediente == null || numeroExpediente.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El numero de expediente es obligatorio.");
        }
        if (nombreDestinatario == null || nombreDestinatario.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del destinatario es obligatorio.");
        }
        if (tipoSala == null || tipoSala.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de sala es obligatorio.");
        }
        if (correoElectronico == null || correoElectronico.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electronico es obligatorio.");
        }
        if (!EMAIL_PATTERN.matcher(correoElectronico.trim()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El formato del correo electronico es invalido.");
        }
        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo es obligatorio.");
        }
    }

    private String sanitizeFilename(String name) {
        if (name == null || name.isBlank())
            return "adjunto";
        // Evita rutas raras tipo C:\... o ../../
        return name.replaceAll("[\\\\/]+", "_");
    }

}
