package mx.gob.pjpuebla.trials.litigante;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/litigante")
@SecurityRequirement(name = "Keycloak")
public class LitiganteResource {
}
