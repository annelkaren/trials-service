package mx.gob.pjpuebla.trials.core.usuarios;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.config.KeycloakSecurityUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/usuarios")
@SecurityRequirement(name = "Keycloak")
public class UsuariosResource {

    private final KeycloakSecurityUtil keycloakSecurityUtil;

    @Value("${keycloak.realm}")
    private String realm;

    @GetMapping
    public List<Usuario> getAll() {
        Keycloak keycloak = this.keycloakSecurityUtil.getKeycloakInstance();
        List<UserRepresentation> users = keycloak.realm(realm).users().list();
        return mapUsers(users);
    }

    @GetMapping("/{id}")
    public Response getById(@PathVariable String id) {
        Keycloak keycloak = this.keycloakSecurityUtil.getKeycloakInstance();
        try {
            UserRepresentation userRepresentation = keycloak.realm(realm).users().get(id).toRepresentation();
            return Response.ok(mapUser(userRepresentation)).build();
        } catch (Exception ex) {
            return Response.ok("Error al obtener el usuario con id " + id).build();
        }
    }

    @PostMapping
    public Response create(@RequestBody Usuario user) {
        UserRepresentation userRepresentation = mapUser(user);
        Keycloak keycloak = this.keycloakSecurityUtil.getKeycloakInstance();
        keycloak.realm(realm).users().create(userRepresentation);
        return Response.ok(user).build();
    }

    @DeleteMapping("/{id}")
    public Response delete(@PathVariable String id) {
        Keycloak keycloak = this.keycloakSecurityUtil.getKeycloakInstance();
        try {
            keycloak.realm(realm).users().get(id).toRepresentation();
            return Response.ok(keycloak.realm(realm).users().delete(id)).build();
        } catch (Exception ex) {
            return Response.ok("El usuario que intentas eliminar no existe.").build();
        }
    }

    private List<Usuario> mapUsers(List<UserRepresentation> userRepresentations) {
        List<Usuario> users = new ArrayList<>();
        userRepresentations.forEach(userRep -> users.add(mapUser(userRep)));
        return users;
    }

    private Usuario mapUser(UserRepresentation userRepresentation) {
        Usuario user = new Usuario();
        user.setId(userRepresentation.getId());
        user.setFirstName(userRepresentation.getFirstName());
        user.setLastName(userRepresentation.getLastName());
        user.setEmail(userRepresentation.getEmail());
        user.setUserName(userRepresentation.getUsername());
        return user;
    }

    private UserRepresentation mapUser(Usuario user) {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername(user.getUserName());
        userRep.setFirstName(user.getFirstName());
        userRep.setLastName(user.getLastName());
        userRep.setEmail(user.getEmail());
        userRep.setEnabled(Boolean.TRUE);
        userRep.setEmailVerified(Boolean.TRUE);
        List<CredentialRepresentation> creds = new ArrayList<>();
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setTemporary(Boolean.FALSE);
        cred.setValue(user.getPassword());
        creds.add(cred);
        userRep.setCredentials(creds);
        return userRep;
    }
}
