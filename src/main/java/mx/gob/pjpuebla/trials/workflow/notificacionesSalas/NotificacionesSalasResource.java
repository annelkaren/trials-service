package mx.gob.pjpuebla.trials.workflow.notificacionesSalas;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionesSalasRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;



@RequiredArgsConstructor
@RestController
@RequestMapping({"/api/workflow/notificacionesSalas", "/api/workflow/notificaciones-sala"})
@SecurityRequirement(name ="Keycloak")
public class NotificacionesSalasResource {
    
    private final NotificacionesSalasServices notificacionesSalasServices;

    @GetMapping("/")
    public Page<NotificacionesSalasRecord> getPageNotificaciones(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "expediente", required = false) String expediente,
            @RequestParam(value = "destino", required = false) String destino,
            @RequestParam(value = "correo", required = false) String correo,
            @RequestParam(value = "fechaEnvioFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime  fechaEnvioFrom,
            @RequestParam(value = "fechaEnvioTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime  fechaEnvioTo,
            @RequestParam(value = "fechaTerminoFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaTerminoFrom,
            @RequestParam(value = "fechaTerminoTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime  fechaTerminoTo
    ) 
    {
        return notificacionesSalasServices.getPageNotificaciones(pageable, key, expediente, destino, correo, fechaEnvioFrom, fechaEnvioTo, fechaTerminoFrom, fechaTerminoTo);
    }

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NotificacionesSalasRecord> createNotificacion(
            @RequestParam("numeroExpediente") String numeroExpediente,
            @RequestParam("tipoSala") String tipoSala,
            @RequestParam("nombreDestinatario") String nombreDestinatario,
            @RequestParam("correoElectronico") String correoElectronico,
            @RequestParam(value = "fechaTermino", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaTermino,
            @RequestParam("archivo") MultipartFile archivo) {
        NotificacionesSalasRecord response = notificacionesSalasServices.createNotificacion(
                numeroExpediente,
                tipoSala,
                nombreDestinatario,
                correoElectronico,
                fechaTermino,
                archivo);
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
    

}
