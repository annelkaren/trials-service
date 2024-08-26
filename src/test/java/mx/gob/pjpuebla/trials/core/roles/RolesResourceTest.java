package mx.gob.pjpuebla.trials.core.roles;

import mx.gob.pjpuebla.trials.config.KeycloakSecurityUtil;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RolesResource.class)
@MockBean(SecurityFilterChain.class)
class RolesResourceTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    KeycloakSecurityUtil keycloakSecurityUtil;


    @Test
    void getAll() throws Exception {
        Keycloak keycloak = Mockito.mock(Keycloak.class);
        RealmResource realmResource = Mockito.mock(RealmResource.class);
        org.keycloak.admin.client.resource.RolesResource rolesResource = Mockito.mock(org.keycloak.admin.client.resource.RolesResource.class);
        given(keycloakSecurityUtil.getKeycloakInstance()).willReturn(keycloak);
        given(keycloak.realm(anyString())).willReturn(realmResource);
        given(realmResource.roles()).willReturn(rolesResource);
        given(rolesResource.list(anyBoolean()))
                .willReturn(List.of(new RoleRepresentation("IMPLEMENTADOR", "Rol de Implementador", false)));

        mockMvc.perform(get("/api/core/roles"))
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$.data.id").value(entity.getId()))
//                .andExpect(jsonPath("$.data.nombre").value(entity.getNombre()));
    }
}