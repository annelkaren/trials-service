package mx.gob.pjpuebla.trials.core.roles;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.config.KeycloakSecurityUtil;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.oficialias.OficialiaRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RoleService {

    private static final String CENTRO_TRABAJO_KEY = "centro-trabajo";
    private static final String JUSTICIA_ADOLESCENTES = "JUSTICIA PARA ADOLESCENTES";
    private static final String PENAL = "PENAL";
    private final AuditorAware<Jwt> auditorAware;
    private final KeycloakSecurityUtil keycloakSecurityUtil;
    private final JuzgadoRepository juzgadoRepository;
    private final OficialiaRepository oficialiaRepository;
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
            boolean isAnExistingRole = newRoles.contains(current.getName());
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
        return roles.stream().anyMatch(current -> current.id().equalsIgnoreCase(role));
    }

    public List<RoleRecord> getAll() {
        Keycloak keycloak = this.keycloakSecurityUtil.getKeycloakInstance();
        List<RoleRepresentation> roles = keycloak.realm(realm).roles().list(false);
        return mapRoles(roles, "", false);
    }

    public List<RoleRecord> getAllAvailablesByUserId(String userId, String tipoCentroTrabajo, boolean isEdicion, Integer centroTrabajoId) {
        List<RoleRepresentation> roles = new ArrayList<>();
        List<RoleRepresentation> filteredList;
        Keycloak keycloak = this.keycloakSecurityUtil.getKeycloakInstance();
        List<RoleRepresentation> allRoles = keycloak.realm(realm).roles().list(false);
        try {
            UserResource userRepresentation = keycloak.realm(realm).users().get(userId);
            if (isEdicion) {
                List<RoleRepresentation> currentRoles = userRepresentation.roles().realmLevel().listAll();
                allRoles.forEach(role -> {
                    boolean isAnExistingRole = currentRoles.stream().anyMatch(current -> role.getName().equalsIgnoreCase(current.getName()));
                    if (!isAnExistingRole) {
                        roles.add(role);
                    }
                });
                filteredList = excludeAdminRoleIfNotApply(roles);
            } else {
                filteredList = excludeAdminRoleIfNotApply(allRoles);
            }
            return mapRoles(filteredList, tipoCentroTrabajo, isPenal(tipoCentroTrabajo, centroTrabajoId));
        } catch (Exception ex) {
            throw new NotFoundException("Usuario no encontrado en keycloak", "usuario");
        }
    }

    private boolean isPenal(String tipoCentroTrabajo, Integer centroTrabajoId) {
        if (tipoCentroTrabajo.equalsIgnoreCase("JUZGADO")) {
            Juzgado juzgado = juzgadoRepository.findById(centroTrabajoId).orElseThrow(() -> new NotFoundException("Juzgado no encontrado", centroTrabajoId.toString()));
            return juzgado.getMateria().getNombre().equalsIgnoreCase(PENAL) ||
                    juzgado.getMateria().getNombre().equalsIgnoreCase(JUSTICIA_ADOLESCENTES);
        }
        if (tipoCentroTrabajo.equalsIgnoreCase("OFICIALIA_COMUN")) {
            Oficialia oficialia = oficialiaRepository.findById(centroTrabajoId).orElseThrow(() -> new NotFoundException("Oficialia no encontrada", centroTrabajoId.toString()));
            int count = getCount(oficialia);
            return count == oficialia.getJuzgados().size() + oficialia.getMaterias().size();
        }
        return false;
    }

    private static int getCount(Oficialia oficialia) {
        int count = 0;
        for (Materia materia : oficialia.getMaterias()) {
            if (materia.getNombre().equalsIgnoreCase(PENAL)
                    || materia.getNombre().equalsIgnoreCase(JUSTICIA_ADOLESCENTES)) {
                count += 1;
            }
        }

        for (Juzgado juzgado : oficialia.getJuzgados()) {
            if (juzgado.getMateria().getNombre().equalsIgnoreCase(PENAL)
                    || juzgado.getMateria().getNombre().equalsIgnoreCase(JUSTICIA_ADOLESCENTES)) {
                count += 1;
            }
        }
        return count;
    }

    private List<RoleRepresentation> excludeAdminRoleIfNotApply(List<RoleRepresentation> roles) {
        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();
        boolean isAdminsystem = hasRole(jwt.getSubject(), "ADMINISTRADOR_SISTEMA");
        if (!isAdminsystem) {
            return roles.stream()
                    .filter(role -> !role.getName().equalsIgnoreCase("ADMINISTRADOR_SISTEMA"))
                    .toList();
        }
        return roles;
    }

    private List<RoleRecord> mapRoles(List<RoleRepresentation> roleRepresentations, String tipoCentroTrabajo, boolean isPenal) {
        List<RoleRecord> roles = new ArrayList<>();
        List<RoleRepresentation> temporalList = roleRepresentations.stream()
                .filter(r -> r.getAttributes() != null)
                .filter(r -> r.getAttributes().containsKey("client-role"))
                .filter(r -> r.getAttributes().get("client-role").contains("true"))
                .filter(r -> r.getAttributes().containsKey(CENTRO_TRABAJO_KEY)).toList();

        if (!tipoCentroTrabajo.isEmpty() && !tipoCentroTrabajo.equalsIgnoreCase("undefined")) {
            temporalList.stream()
                    .filter(r -> r.getAttributes().get(CENTRO_TRABAJO_KEY).contains(tipoCentroTrabajo)
                            || r.getAttributes().get(CENTRO_TRABAJO_KEY).contains("-"))
                    .forEach(r -> roles.add(mapRole(r)));
        } else {
            temporalList.forEach(r -> roles.add(mapRole(r)));
        }
        roles.sort(Comparator.comparing(RoleRecord::id));
        if (isPenal) {
            return renameRoles(roles);
        }
        return roles;
    }

    private RoleRecord mapRole(RoleRepresentation roleRepresentation) {
        return new RoleRecord(roleRepresentation.getName(), roleRepresentation.getDescription());
    }

    private List<RoleRecord> renameRoles(List<RoleRecord> roles) {
        List<RoleRecord> newList = new ArrayList<>();
        try {
            String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            String defaultConfigPath = rootPath + "roles.properties";
            Properties defaultProps = new Properties();
            defaultProps.load(new FileInputStream(defaultConfigPath));
            String[] array = {"OFICIAL_MAYOR_JUZGADO", "AUXILIAR_OFICIAL_MAYOR_JUZGADO", "SECRETARIO", "DILIGENCIARIO"};
            for (RoleRecord role : roles) {
                boolean applyRename = Arrays.stream(array).anyMatch(role.id()::equals);
                if (applyRename) {
                    newList.add(new RoleRecord(role.id(), defaultProps.get(role.id()).toString()));
                } else {
                    newList.add(role);
                }
            }
        } catch (IOException e) {
            return roles;
        }
        return newList;
    }
}
