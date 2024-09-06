package mx.gob.pjpuebla.trials.core.usuarios;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.config.KeycloakSecurityUtil;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        Object response = keycloak.realm(realm).users().create(userRepresentation).getEntity();
        return "annelkaren";
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
