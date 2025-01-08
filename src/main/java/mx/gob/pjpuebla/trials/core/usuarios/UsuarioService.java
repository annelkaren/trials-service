package mx.gob.pjpuebla.trials.core.usuarios;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.config.KeycloakSecurityUtil;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.UnauthorizedException;
import mx.gob.pjpuebla.trials.error.UserAlreadyExistException;
import mx.gob.pjpuebla.trials.util.EmailService;
import org.apache.commons.lang3.RandomStringUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
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
    private final EmailService emailService;

    public String create(Persona persona) {
        UserRepresentation userRepresentation = mapUser(persona);
        Keycloak keycloak = this.keycloakSecurityUtil.getKeycloakInstance();
        try (Response response = keycloak.realm(keycloakSecurityUtil.realm).users().create(userRepresentation)) {
            if (response.getStatus() == HttpStatus.CREATED.value()) {
                sendMail(userRepresentation.getEmail(),
                        userRepresentation.getCredentials().get(0).getValue(),
                        userRepresentation.getFirstName() + " " + userRepresentation.getLastName());
                return response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            } else {
                throw new UserAlreadyExistException("El correo electrónico proporcionado ya se encuentra registrado", persona.getCorreoElectronico());
            }
        }
    }

    public List<String> findAllByRoles(List<String> roleNames) {
        List<String> jueces = new ArrayList<>();
        Keycloak keycloak = this.keycloakSecurityUtil.getKeycloakInstance();
        List<UserRepresentation> users = keycloak.realm(keycloakSecurityUtil.realm).users().list();

        for (UserRepresentation user : users) {
            List<RoleRepresentation> roles = keycloak.realm(keycloakSecurityUtil.realm).users().get(user.getId()).roles().realmLevel().listAll();
            if (roles.stream().anyMatch(role -> roleNames.stream().anyMatch(roleName -> role.getName().equalsIgnoreCase(roleName)))) {
                jueces.add(user.getId());
            }
        }

        return jueces;
    }

    public String findByUsernameAndRol(String username, String rol) {
        Keycloak keycloak = this.keycloakSecurityUtil.getKeycloakInstance();
        List<UserRepresentation> users = keycloak.realm(keycloakSecurityUtil.realm).users().searchByUsername(username, true);
        UserRepresentation user;
        if (!users.isEmpty()) {
            user = users.get(0);//No es posible tener más de un usuario con el mismo username
            List<RoleRepresentation> roles = keycloak.realm(keycloakSecurityUtil.realm).users().get(user.getId()).roles().realmLevel().listAll();
            if (roles.stream().anyMatch(roleName -> roleName.getName().equalsIgnoreCase(rol))) {
                return user.getId();
            } else {
                throw new UnauthorizedException("No tiene permiso para acceder a este portal", "".concat(username));
            }
        } else {
            throw new NotFoundException("Persona no encontrada", "".concat(username));
        }
    }

    private UserRepresentation mapUser(Persona persona) {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername(persona.getCorreoElectronico().trim());
        userRep.setFirstName(persona.getNombre());
        userRep.setLastName(persona.getApellidoPaterno());
        userRep.setEmail(persona.getCorreoElectronico());
        userRep.setEnabled(Boolean.TRUE);
        userRep.setEmailVerified(Boolean.TRUE);
        List<CredentialRepresentation> creds = new ArrayList<>();
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setTemporary(Boolean.TRUE);
        cred.setValue(RandomStringUtils.randomAlphanumeric(10));
        creds.add(cred);
        userRep.setCredentials(creds);
        return userRep;
    }

    public void sendMail(String email, String password, String name) {
        Map<String, Object> sendEmail = new HashMap<>();

        sendEmail.put("name", name);
        sendEmail.put("username", email);
        sendEmail.put("password", password);

        emailService.sendMail(
                List.of(email),
                Collections.emptyList(),
                Collections.emptyList(),
                "¡Bienvenido(a) a nuestro portal!",
                "welcome.ftl",
                sendEmail
        );

    }
}
