package mx.gob.pjpuebla.trials.core.usuarios;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.config.KeycloakSecurityUtil;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.error.UserAlreadyExistException;
import org.keycloak.admin.client.Keycloak;
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
        try (Response response = keycloak.realm(realm).users().create(userRepresentation)) {
            if (response.getStatus() == HttpStatus.CREATED.value()) {
                return response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            } else {
                throw new UserAlreadyExistException("Usuario existente", persona.getCorreoElectronico());
            }
        }
    }

    public List<String> findAllByRoles(List<String> roleNames) {
        List<String> jueces = new ArrayList<>();
        Keycloak keycloak = this.keycloakSecurityUtil.getKeycloakInstance();
        List<UserRepresentation> users = keycloak.realm(realm).users().list();

        for (UserRepresentation user : users) {
            List<RoleRepresentation> roles = keycloak.realm(realm).users().get(user.getId()).roles().realmLevel().listAll();
            if (roles.stream().anyMatch(role -> roleNames.stream().anyMatch(roleName -> role.getName().equalsIgnoreCase(roleName)))) {
                jueces.add(user.getId());
            }
        }

        return jueces;
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
