package mx.gob.pjpuebla.trials.core.usuarios;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.config.KeycloakSecurityUtil;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.error.UserAlreadyExistException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UsuarioService {

    private final KeycloakSecurityUtil keycloakSecurityUtil;

    @Value("${keycloak.realm}")
    private String realm;

    public String create(Persona persona) {
        UserRepresentation userRepresentation = mapUser(persona);
        Keycloak keycloak = this.keycloakSecurityUtil.getKeycloakInstance();
        Response response = keycloak.realm(realm).users().create(userRepresentation);
        if (response.getStatus() == HttpStatus.CREATED.value()) {
            return response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        } else {
            throw new UserAlreadyExistException("Usuario existente", persona.getCorreoElectronico());
        }
    }

    public void addRoles(String userId, List<String> newRoles) {
        List<RoleRepresentation> selectedRoles = new ArrayList<>();
        Keycloak keycloak = this.keycloakSecurityUtil.getKeycloakInstance();
        UserResource userRepresentation = keycloak.realm(realm).users().get(userId);
        for (String newRole : newRoles) {
            RoleRepresentation role = keycloak.realm(realm).roles().get(newRole).toRepresentation();
            selectedRoles.add(role);
        }
        userRepresentation.roles().realmLevel().add(selectedRoles);
    }

    public void updateRoles(String userId, List<String> newRoles) {
        List<RoleRepresentation> rolesToRemove = new ArrayList<>();
        Keycloak keycloak = this.keycloakSecurityUtil.getKeycloakInstance();
        UserResource userRepresentation = keycloak.realm(realm).users().get(userId);
        List<RoleRepresentation> currentRoles = userRepresentation.roles().realmLevel().listAll();

        for (RoleRepresentation current : currentRoles) {
            boolean isAnExistingRole = newRoles.stream().filter(role -> role.equalsIgnoreCase(current.getName())).findFirst().isPresent();
            if (!isAnExistingRole && !current.getName().toLowerCase().contains("default")) {
                rolesToRemove.add(current);
            }
        }
        userRepresentation.roles().realmLevel().remove(rolesToRemove);
        addRoles(userId, newRoles);
    }

    private UserRepresentation mapUser(Persona persona) {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername(persona.getCorreoElectronico());
        userRep.setFirstName(persona.getNombre());
        userRep.setLastName(persona.getApellidoPaterno());
        userRep.setEmail(persona.getCorreoElectronico());
        userRep.setEnabled(Boolean.TRUE);
        userRep.setEmailVerified(Boolean.TRUE);
        List<CredentialRepresentation> creds = new ArrayList<>();
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setTemporary(Boolean.TRUE);
        creds.add(cred);
        userRep.setCredentials(creds);
        return userRep;
    }
}
