package mx.gob.pjpuebla.trials.workflow.notificaciones;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.NotificacionDto;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.NotificacionResponseRecord;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.NotificacionSaveRecord;

import mx.gob.pjpuebla.trials.workflow.notificaciones.records.ListaResponse;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.NotaResponse;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.NotificacionRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name = "Keycloak")
public class NotificacionResource {

    private final NotificacionService notificacionService;

    @GetMapping(value = "/bandeja/notificaciones", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<NotificacionRecord> getAllNotificaciones(@PageableDefault(size = 20) Pageable pageable,
                                                         @RequestParam(value = "tipo", required = false) String tipo,
                                                         @RequestParam(value = "estado", required = false) String estado) {
        return this.notificacionService.getAllNotificaciones(tipo, estado, pageable);
    }


    @PostMapping(value = "/bandeja/notificaciones/createNota", produces = MediaType.APPLICATION_JSON_VALUE)
    public void createNotaNotificacion(@RequestBody NotaResponse notaResponse) {
        notificacionService.createNotaNotificacion(notaResponse.id(), notaResponse.notas());
    }


    @PostMapping(value = "/bandeja/notificaciones/createLista", produces = MediaType.APPLICATION_JSON_VALUE)
    public void createListaEstrado(@RequestBody ListaResponse listaResponse) {
        notificacionService.createListaEstrado(listaResponse.notificacionIds(), listaResponse.fechaVencimiento());
    }

    @PostMapping("notificaciones/create")
    public ResponseEntity<String> create(@RequestBody NotificacionDto notificacion) {
        try {
            notificacionService.create(notificacion);
            return ResponseEntity.ok("Notificación registrada con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar la notificación: " + e.getMessage());
        }
    }

    @PostMapping("/documentos/enviarNotificacion")
    public NotificacionResponseRecord createRegistroNotificacion(@RequestBody NotificacionSaveRecord notificacion){
        return notificacionService.createRegistroNotificacion(notificacion);
    }

}

