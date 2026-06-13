package mx.gob.pjpuebla.trials.core.menu;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import mx.gob.pjpuebla.trials.workflow.etiquetas.EtiquetaRepository;
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
    private final EtiquetaRepository etiquetaRepository;

    public List<MenuNode> getMenuByUser() {
        List<MenuNode> newMenu = new ArrayList<>();
        Persona persona = personaService.getAuditor();
        Oficialia oficialia = persona.getOficialia();
        Juzgado juzgado = persona.getJuzgado();

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

        boolean usarCarpeta = usarCarpeta(oficialia);
        if (usarCarpeta) {
            cambiarDemandaPorCarpeta(newMenu);
        }

        if ((oficialia != null && oficialia.getEstado() == Estado.INACTIVE) || 
            (juzgado != null && juzgado.getEstado() == Estado.INACTIVE)) {
            newMenu = newMenu.stream()
                    .filter(menuNode -> !"Registro".equalsIgnoreCase(menuNode.getName()))
                    .collect(Collectors.toList());
        }
        return newMenu;
    }

    private boolean usarCarpeta(Oficialia oficialia) {
        if (oficialia == null ||
                oficialia.getJuzgados() == null ||
                oficialia.getJuzgados().isEmpty()) {
            return false;
        }

        return oficialia.getJuzgados().stream()
                .allMatch(this::esMateriaPenalOAdolescentes);
    }

    private boolean esMateriaPenalOAdolescentes(Juzgado juzgado) {
        String materia = juzgado.getMateria().getNombre();
        return "PENAL".equalsIgnoreCase(materia)
                || "JUSTICIA PARA ADOLESCENTES".equalsIgnoreCase(materia);
    }

    private void cambiarDemandaPorCarpeta(List<MenuNode> menus) {
        for (MenuNode menu : menus) {
            if (menu.getItems() != null) {
                for (MenuNode child : menu.getItems()) {
                    if ("Demandas".equalsIgnoreCase(child.getName())) {
                        child.setName("Causas");
                    }
                }
            }
        }
    }
}
