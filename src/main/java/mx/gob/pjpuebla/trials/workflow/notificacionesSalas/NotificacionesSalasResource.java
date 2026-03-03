package mx.gob.pjpuebla.trials.workflow.notificacionesSalas;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaCreateRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaCreateResponseRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaDetalleRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionesSalasRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping({ "/api/workflow/notificacionesSalas", "/api/workflow/notificaciones-sala" })
@SecurityRequirement(name = "Keycloak")
public class NotificacionesSalasResource {

    private final NotificacionesSalasServices notificacionesSalasServices;

    @GetMapping("/")
    public Page<NotificacionesSalasRecord> getPageNotificaciones(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "numeroExpediente", required = false) String numeroExpediente,
            @RequestParam(value = "tipoSala", required = false) String tipoSala,
            @RequestParam(value = "nombreDestinatario", required = false) String nombreDestinatario,
            @RequestParam(value = "correoElectronico", required = false) String correoElectronico,
            @RequestParam(value = "fechaEnvioFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaEnvioFrom,
            @RequestParam(value = "fechaEnvioTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaEnvioTo,
            @RequestParam(value = "fechaTerminoFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaTerminoFrom,
            @RequestParam(value = "fechaTerminoTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaTerminoTo) {
        return notificacionesSalasServices.getPageNotificaciones(pageable, q, numeroExpediente, nombreDestinatario,
                correoElectronico, fechaEnvioFrom, fechaEnvioTo, fechaTerminoFrom, fechaTerminoTo, tipoSala);
    }

    @GetMapping("/{idNotificacionSala}")
    public NotificacionSalaDetalleRecord getDetalle(@PathVariable Integer idNotificacionSala) {
        return notificacionesSalasServices.getDetalle(idNotificacionSala);
    }

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NotificacionSalaCreateResponseRecord> createNotificacion(
            @RequestPart("notificacionSalaJson") NotificacionSalaCreateRecord request,
            @RequestPart("archivo") MultipartFile archivo) {

        NotificacionSalaCreateResponseRecord response = notificacionesSalasServices.createNotificacion(request, archivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{idNotificacionSala}/archivo")
    public ResponseEntity<byte[]> downloadArchivo(@PathVariable Integer idNotificacionSala) throws java.io.IOException {
        byte[] file = notificacionesSalasServices.downloadArchivo(idNotificacionSala);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("adjunto", idNotificacionSala + "_notificacion_sala");
        return ResponseEntity.ok().headers(headers).body(file);
    }

    @GetMapping(value = "/download/{nombreArchivo}")
    public ResponseEntity<byte[]> downloadArchivoPublico(@PathVariable String nombreArchivo) {
        byte[] file = notificacionesSalasServices.downloadArchivoPublico(nombreArchivo);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("adjunto", nombreArchivo);
        return ResponseEntity.ok().headers(headers).body(file);
    }
}
