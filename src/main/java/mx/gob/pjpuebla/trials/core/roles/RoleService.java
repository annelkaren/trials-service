package mx.gob.pjpuebla.trials.core.roles;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.config.KeycloakSecurityUtil;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RoleService {

    private final KeycloakSecurityUtil keycloakSecurityUtil;

    @Value("${keycloak.realm}")
    private String realm;

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
            boolean isAnExistingRole = newRoles.stream().anyMatch(role -> role.equalsIgnoreCase(current.getName()));
            if (!isAnExistingRole && !current.getName().toLowerCase().contains("default")) {
                rolesToRemove.add(current);
            }
        }
        userRepresentation.roles().realmLevel().remove(rolesToRemove);
        addRoles(userId, newRoles);
    }

    public List<RoleRecord> getRolesByUserId(String userId) {
        List<RoleRecord> roles = new ArrayList<>();
        Keycloak keycloak = this.keycloakSecurityUtil.getKeycloakInstance();
        try {
            UserResource userRepresentation = keycloak.realm(realm).users().get(userId);
            List<RoleRepresentation> currentRoles = userRepresentation.roles().realmLevel().listAll();
            currentRoles.forEach(role -> {
                if (!role.getName().toLowerCase().contains("default"))
                    roles.add(mapRole(role));
            });
        } catch (Exception ex) {
            throw new NotFoundException("Usuario no encontrado en keycloak", "usuario");
        }
        return roles;
    }

    public boolean hasRole(String userId, String role) {
        List<RoleRecord> roles = getRolesByUserId(userId);
        return roles.stream().anyMatch(current -> current.name().equalsIgnoreCase(role));
    }

    public List<RoleRecord> getAllAvailablesByUserId(String userId) {
        List<RoleRepresentation> roles = new ArrayList<>();
        Keycloak keycloak = this.keycloakSecurityUtil.getKeycloakInstance();
        List<RoleRepresentation> allRoles = keycloak.realm(realm).roles().list(false);
        try {
            UserResource userRepresentation = keycloak.realm(realm).users().get(userId);
            List<RoleRepresentation> currentRoles = userRepresentation.roles().realmLevel().listAll();
            allRoles.forEach(role -> {
                boolean isAnExistingRole = currentRoles.stream().anyMatch(current -> role.getName().equalsIgnoreCase(current.getName()));
                if (!isAnExistingRole && !role.getName().toLowerCase().contains("default")) {
                    roles.add(role);
                }
            });
            return mapRoles(roles);
        } catch (Exception ex) {
            throw new NotFoundException("Usuario no encontrado en keycloak", "usuario");
        }
    }

    private List<RoleRecord> mapRoles(List<RoleRepresentation> roleRepresentations) {
        List<RoleRecord> roles = new ArrayList<>();
        roleRepresentations.stream()
                .filter(r -> r.getAttributes() != null)
                .filter(r -> r.getAttributes().containsKey("client-role"))
                .filter(r -> r.getAttributes().get("client-role").contains("true"))
                .forEach(r -> roles.add(mapRole(r)));
        return roles;
    }

    private RoleRecord mapRole(RoleRepresentation roleRepresentation) {
        return new RoleRecord(roleRepresentation.getName(), roleRepresentation.getName());
    }
}
