package mx.gob.pjpuebla.trials.workflow.notificacionesSalas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.enums.EstadoEnvioNotificacionesSalas;
import mx.gob.pjpuebla.trials.workflow.documentos.DigitalizacionService;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DigitalizacionRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionesSalasRecord;

@RequiredArgsConstructor
@Service
public class NotificacionesSalasServices {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final NotificacionesSalasRepository notificacionesSalasRepository;
    private final DigitalizacionService digitalizacionService;

    public Page<NotificacionesSalasRecord> getPageNotificaciones(Pageable pageable, String key, String expediente,
            String destino, String correo, LocalDateTime fechaEnvioFrom, LocalDateTime fechaEnvioTo,
            LocalDateTime fechaTerminoFrom, LocalDateTime fechaTerminoTo) {

        return notificacionesSalasRepository.getPageNotificaciones(pageable)
                .map(item -> new NotificacionesSalasRecord(
                        item.idNotificacionSala(),
                        item.numeroExpediente(),
                        item.tipoSala(),
                        item.nombreDestinatario(),
                        item.correoElectronico(),
                        item.fechaTermino(),
                        item.rutaArchivo(),
                        item.rutaArchivo(),
                        item.fechaEnvio(),
                        item.fechaLectura(),
                        item.fechaEntrega()));
    }

    @Transactional
    public NotificacionesSalasRecord createNotificacion(String numeroExpediente, String tipoSala,
            String nombreDestinatario,
            String correoElectronico, LocalDate fechaTermino, MultipartFile archivo) {
        
        validaciones(numeroExpediente, tipoSala, nombreDestinatario, correoElectronico, archivo);
            
        NotificacionesSalas notificacion = new NotificacionesSalas()
            .setExpediente(numeroExpediente.trim())
            .setTipoSala(tipoSala.trim())
            .setNombreDestinatario(nombreDestinatario.trim())
            .setCorreoDestinatario(correoElectronico.trim())
            .setFechaTermino(fechaTermino != null ? fechaTermino.atStartOfDay() : null)
            .setFechaEnvio(LocalDateTime.now())
            .setEstado(EstadoEnvioNotificacionesSalas.ENVIADO);

        notificacion = notificacionesSalasRepository.save(notificacion);

        DigitalizacionRecord digitalizacionRecord = digitalizacionService
            .guardarArchivoNotificacionSala(archivo, notificacion.getId(), tipoSala);

        notificacion.setRutaArchivo(digitalizacionRecord.nombreArchivo());
        notificacion = notificacionesSalasRepository.save(notificacion);

        //Aqui intentamos enviar el correo con SEND PULS:
        

        return new NotificacionesSalasRecord(
                notificacion.getId(),
                notificacion.getExpediente(),
                notificacion.getTipoSala(),
                notificacion.getNombreDestinatario(),
                notificacion.getCorreoDestinatario(),
                notificacion.getFechaTermino(),
                notificacion.getRutaArchivo(),
                notificacion.getRutaArchivo(),
                notificacion.getFechaEnvio(),
                notificacion.getFechaLectura(),
                notificacion.getFechaEntrega());
    }

    public byte[] downloadArchivo(Integer idNotificacionSala) throws java.io.IOException {
        NotificacionesSalas notificacion = notificacionesSalasRepository.findById(idNotificacionSala)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificacion no encontrada."));

        if (notificacion.getRutaArchivo() == null || notificacion.getRutaArchivo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La notificacion no tiene archivo.");
        }
        return digitalizacionService.getArchivoNotificacionSala(notificacion.getRutaArchivo(), notificacion.getTipoSala());
    }

    private void validaciones(String numeroExpediente, String tipoSala, String nombreDestinatario, String correoElectronico, MultipartFile archivo) {
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

}
