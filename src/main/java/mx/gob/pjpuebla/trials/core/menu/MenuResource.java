package mx.gob.pjpuebla.trials.core.menu;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/menu")
@SecurityRequirement(name = "Keycloak")
public class MenuResource {

    private final MenuService menuService;

    @GetMapping
    public List<MenuNode> getMenuByUser() {
        return menuService.getMenuByUser();
    }
}
