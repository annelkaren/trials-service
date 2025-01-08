package mx.gob.pjpuebla.trials.core.usuarios;

import mx.gob.pjpuebla.trials.config.KeycloakSecurityUtil;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    public KeycloakSecurityUtil keycloakSecurityUtil;
    @InjectMocks
    public UsuarioService usuarioService;

    Keycloak keycloak = Mockito.mock(Keycloak.class);
    RealmResource realmResource = Mockito.mock(RealmResource.class);
    UsersResource usersResource = Mockito.mock(UsersResource.class);
    UserResource userResource = Mockito.mock(UserResource.class);
    RoleMappingResource roleMappingResource = Mockito.mock(RoleMappingResource.class);
    RoleScopeResource roleScopeResource = Mockito.mock(RoleScopeResource.class);

    @Test
    void findByUsernameAndRol_success() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId("d0e98e2a-d034-49f2-acbb-70d83007f632");

        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName("LITIGANTE");

        given(keycloakSecurityUtil.getKeycloakInstance()).willReturn(keycloak);
        given(keycloak.realm(any())).willReturn(realmResource);
        given(realmResource.users()).willReturn(usersResource);
        given(usersResource.searchByUsername(any(), any())).willReturn(Collections.singletonList(userRepresentation));
        given(usersResource.get(any())).willReturn(userResource);
        given(userResource.roles()).willReturn(roleMappingResource);
        given(roleMappingResource.realmLevel()).willReturn(roleScopeResource);
        given(roleScopeResource.listAll()).willReturn(Collections.singletonList(roleRepresentation));
        String user = usuarioService.findByUsernameAndRol("test", "LITIGANTE");

        assertThat(user).isEqualTo(userRepresentation.getId());
    }

    @Test
    void findByUsernameAndRol_UnauthorizedException() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId("d0e98e2a-d034-49f2-acbb-70d83007f632");

        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName("LITIGANTE");

        given(keycloakSecurityUtil.getKeycloakInstance()).willReturn(keycloak);
        given(keycloak.realm(any())).willReturn(realmResource);
        given(realmResource.users()).willReturn(usersResource);
        given(usersResource.searchByUsername(any(), any())).willReturn(Collections.singletonList(userRepresentation));
        given(usersResource.get(any())).willReturn(userResource);
        given(userResource.roles()).willReturn(roleMappingResource);
        given(roleMappingResource.realmLevel()).willReturn(roleScopeResource);

        UnauthorizedException assertThrows = assertThrows(
                UnauthorizedException.class,
                () -> usuarioService.findByUsernameAndRol("test", "LITIGANTE")
        );
        assertThat(assertThrows.getMessage()).contains("No tiene permiso para acceder a este portal");
    }

    @Test
    void findByUsernameAndRol_NotFoundException() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId("d0e98e2a-d034-49f2-acbb-70d83007f632");

        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName("LITIGANTE");

        given(keycloakSecurityUtil.getKeycloakInstance()).willReturn(keycloak);
        given(keycloak.realm(any())).willReturn(realmResource);
        given(realmResource.users()).willReturn(usersResource);

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> usuarioService.findByUsernameAndRol("test", "LITIGANTE")
        );
        assertThat(assertThrows.getMessage()).contains("Persona no encontrada");
    }
}
