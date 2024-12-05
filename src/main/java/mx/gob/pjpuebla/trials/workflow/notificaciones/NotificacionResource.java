package mx.gob.pjpuebla.trials.workflow.notificaciones;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name = "Keycloak")
public class NotificacionResource {

    private final NotificacionService notificacionService;

    @GetMapping( value = "/bandeja/notificaciones" ,  produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<NotificacionRecord> getAllNotificaciones(@PageableDefault(size = 20) Pageable pageable,
                                                         @RequestParam(value = "key", required = false) String key) {
        return this.notificacionService.getAllNotificaciones(key, pageable);
    }

}

