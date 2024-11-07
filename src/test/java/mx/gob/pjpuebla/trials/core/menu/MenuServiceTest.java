package mx.gob.pjpuebla.trials.core.menu;

import mx.gob.pjpuebla.trials.core.bloques.BloqueRepository;
import mx.gob.pjpuebla.trials.core.bloques.BloqueService;
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.conceptos.ConceptoRecordResponse;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;
    @Mock
    PersonaService personaService;
    @Mock
    RoleService roleService;

    @InjectMocks
    private MenuService menuService;

    List<Menu> menus;

    @BeforeEach
    public void setUp() {
        menus = new ArrayList<>();
        Menu menu = new Menu().setId(1).setNombre("Catálogo").setLink("").setOrder(1).setRoles("ADMINISTRADOR");
        Menu menu1 = new Menu().setId(2).setNombre("Bandeja").setLink("").setOrder(2);
        Menu menu2 = new Menu().setId(3).setNombre("Sedes").setLink("/api/core/sedes").setOrder(1)
                .setParent(1).setRoles("ADMINISTRADOR");;
        menus.add(menu);
        menus.add(menu1);
        menus.add(menu2);
    }

    @Test
    void get_menu_by_user() {
        RoleRecord roleRecord = new RoleRecord("ADMINISTRADOR", "ADMINISTRADOR");
        given(personaService.getAuditor()).willReturn(PersonaSetUp.createPersona());
        given(roleService.getRolesByUserId(any())).willReturn(List.of(roleRecord));
        given(menuRepository.findMenus(any())).willReturn(menus);

        List<MenuNode> list = menuService.getMenuByUser();
        assertThat(list.size()).isPositive();
        assertThat(list).hasSize(2);
    }
}
