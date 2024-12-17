package mx.gob.pjpuebla.trials.core.menu;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final PersonaService personaService;
    private final RoleService roleService;

    public List<MenuNode> getMenuByUser() {
        List<MenuNode> newMenu = new ArrayList<>();
        Persona persona = personaService.getAuditor();
        Oficialia oficialia = persona.getOficialia();
     
        List<RoleRecord> userRoles = roleService.getRolesByUserId(persona.getUsuario());
        String roles = userRoles.stream()
                .map(RoleRecord::id)
                .collect(Collectors.joining("|"));
        List<Menu> menus = menuRepository.findMenus("(TODOS|" + roles + ")");
        menus.sort(Comparator.comparing(Menu::getOrder));
        for (Menu menu : menus) {//parent
            MenuNode menuRecord;
            if (menu.getParent() == null) {
                menuRecord = new MenuNode(menu.getId(), menu.getNombre(), menu.getLink());
                newMenu.add(menuRecord);
            }
        }
        for (MenuNode item : newMenu) {//children
            item.setItems(new ArrayList<>());
            for (Menu menu : menus) {
                if (menu.getParent() != null && menu.getParent().equals(item.getId())) {
                    item.getItems().add(new MenuNode(menu.getId(), menu.getNombre(), menu.getLink()));
                }
            }
        }

        if (oficialia != null && oficialia.getEstado() == Estado.INACTIVE) {
            newMenu = newMenu.stream()
                    .filter(menuNode -> !"Registro".equalsIgnoreCase(menuNode.getName()))
                    .collect(Collectors.toList());
        }
        return newMenu;
    }
}
