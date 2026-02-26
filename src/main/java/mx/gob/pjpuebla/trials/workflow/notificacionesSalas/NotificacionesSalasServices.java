package mx.gob.pjpuebla.trials.workflow.notificacionesSalas;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.util.enums.EstadoEnvioNotificacionesSalas;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionesSalasRecord;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificacionesSalasServices {

    @Value("${app.root-folder}")
    private String rootFolder;

    private static final long MAX_FILE_SIZE = 50L * 1024L * 1024L;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final NotificacionesSalasRepository notificacionesSalasRepository;

    public Page<NotificacionesSalasRecord> getPageNotificaciones(Pageable pageable, String key, String expediente,
            String destino, String correo, LocalDateTime fechaEnvioFrom, LocalDateTime fechaEnvioTo,
            LocalDateTime fechaTerminoFrom, LocalDateTime fechaTerminoTo) {

        return notificacionesSalasRepository.getPageNotificaciones(pageable);
    }

    @Transactional
    public NotificacionesSalasRecord createNotificacion(
            String numeroExpediente,
            String nombreDestinatario,
            String correoElectronico,
            LocalDate fechaTermino,
            MultipartFile archivo) {

        if (numeroExpediente == null || numeroExpediente.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El numero de expediente es obligatorio.");
        }
        if (nombreDestinatario == null || nombreDestinatario.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del destinatario es obligatorio.");
        }
        if (correoElectronico == null || correoElectronico.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electronico es obligatorio.");
        }
        if (!EMAIL_PATTERN.matcher(correoElectronico.trim()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El formato del correo electronico es invalido.");
        }
        if (archivo != null && !archivo.isEmpty() && archivo.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo no debe exceder 50 MB.");
        }

        NotificacionesSalas notificacion = new NotificacionesSalas();
        notificacion.setExpediente(numeroExpediente.trim());
        notificacion.setNombreDestinatario(nombreDestinatario.trim());
        notificacion.setCorreoDestinatario(correoElectronico.trim());
        notificacion.setFechaTermino(fechaTermino != null ? fechaTermino.atStartOfDay() : null);
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacion.setEstado(EstadoEnvioNotificacionesSalas.ENVIADO);

        notificacion = notificacionesSalasRepository.save(notificacion);

        if (archivo != null && !archivo.isEmpty()) {
            String rutaArchivo = guardarArchivo(archivo, notificacion.getId());
            notificacion.setRutaArchivo(rutaArchivo);
            notificacion = notificacionesSalasRepository.save(notificacion);
        }

        return new NotificacionesSalasRecord(
                notificacion.getId(),
                notificacion.getExpediente(),
                notificacion.getNombreDestinatario(),
                notificacion.getCorreoDestinatario(),
                notificacion.getFechaTermino(),
                notificacion.getRutaArchivo(),
                notificacion.getRutaArchivo(),
                notificacion.getFechaEnvio(),
                notificacion.getFechaLectura(),
                notificacion.getFechaEntrega());
    }

    private String guardarArchivo(MultipartFile archivo, Integer notificacionId) {
        String originalFilename = archivo.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }

        String fileName = "notificacion_sala_" + notificacionId + "_" + UUID.randomUUID() + extension;
        String relativePath = "notificacionesSalas/" + LocalDate.now().getYear() + "/" + notificacionId + "/"
                + fileName;

        Path fullPath = Paths.get(rootFolder, "digitalizacion", relativePath);

        try {
            Files.createDirectories(fullPath.getParent());
            Files.write(fullPath, archivo.getBytes());
            return relativePath;
        } catch (IOException exception) {
            log.error("No se pudo guardar el archivo de la notificacion de sala {}", notificacionId, exception);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo guardar el archivo de la notificacion.");
        }
    }
}
