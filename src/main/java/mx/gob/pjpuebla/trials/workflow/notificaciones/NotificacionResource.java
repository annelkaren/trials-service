package mx.gob.pjpuebla.trials.workflow.notificaciones;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.*;
import net.sf.jasperreports.engine.JRException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name = "Keycloak")
public class NotificacionResource {

    private final NotificacionService notificacionService;
    private final ListadoExpedientesRutaService listadoExpedientesRutaService;

    @GetMapping(value = "/bandeja/notificaciones", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<NotificacionRecord> getAllNotificaciones(@PageableDefault(size = 20) Pageable pageable,
                                                         @RequestParam(value = "tipo", required = false) String tipo,
                                                         @RequestParam(value = "estado", required = false) String estado) {
        return this.notificacionService.getAllNotificaciones(tipo, estado, pageable);
    }

    @GetMapping(value = "/bandeja/notificaciones/detalle/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificacionDetalleRecord> getNotificacionDetalle(@PathVariable Integer id) {
        return ResponseEntity.ok(this.notificacionService.getNotificacionDetalle(id));
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

    @PatchMapping("/bandeja/notificaciones/{estado}")
    public void updateBatchNotificacionSalida(@PathVariable String estado, @RequestBody List<Integer> ids) {
        notificacionService.updateBatchNotificacionEnRuta(ids, estado);
    }

    @GetMapping("/notificaciones/reporteListaExpedientes")
    public ResponseEntity<byte[]> getFileListaExpedientes() throws IOException, JRException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("listaExpedientes", "_.pdf");
        return ResponseEntity.ok().headers(headers).body(listadoExpedientesRutaService.exportToPdf());
    }

    @GetMapping("/acuerdo/notificaciones/{idNotificacion}")
    public Page<AcuerdoNotificacionesRecord> acuerdoNotificaciones(
            @PathVariable Integer idNotificacion,
            Pageable pageable) {
        return this.notificacionService.acuerdoNotificaciones(idNotificacion, pageable);
    }
    

}

