package mx.gob.pjpuebla.trials.core.roles;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.config.KeycloakSecurityUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/roles")
@SecurityRequirement(name = "Keycloak")
public class RolesResource {

    private final KeycloakSecurityUtil keycloakSecurityUtil;

    @Value("${keycloak.realm}")
    private String realm;

    @GetMapping
    public List<RoleRecord> getAll() {
        Keycloak keycloak = this.keycloakSecurityUtil.getKeycloakInstance();
        List<RoleRepresentation> roles = keycloak.realm(realm).roles().list(false);
        return mapRoles(roles);
    }

    @GetMapping("/{name}")
    public Response getByName(@PathVariable String name) {
        Keycloak keycloak = this.keycloakSecurityUtil.getKeycloakInstance();
        try {
            RoleRepresentation role = keycloak.realm(realm).roles().get(name).toRepresentation();
            return Response.ok(mapRole(role)).build();
        } catch (Exception ex) {
            log.error("getByName", ex);
            return Response.ok("El rol no existe").build();
        }
    }

    @PostMapping
    public Response create(@RequestBody Role role) {
        RoleRepresentation roleRepresentation = mapRole(role);
        Keycloak keycloak = this.keycloakSecurityUtil.getKeycloakInstance();
        try {
            keycloak.realm(realm).roles().create(roleRepresentation);
        } catch (Exception ex) {
            log.error("create", ex);
            if (ex.getMessage().contains("Conflict")) {//Nombre repetido
                return Response.ok("El rol " + role.getName() + " ya se encuentra registrado. Intenta con otro.").build();
            } else {
                return Response.ok("Error al crear el role").build();
            }
        }
        return Response.ok(role).build();
    }

    @DeleteMapping("/{name}")
    public Response delete(@PathVariable String name) {
        Keycloak keycloak = this.keycloakSecurityUtil.getKeycloakInstance();
        try {
            keycloak.realm(realm).roles().get(name).toRepresentation();
            keycloak.realm(realm).roles().deleteRole(name);
            return Response.ok("Rol eliminado").build();
        } catch (Exception ex) {
            log.error("delete", ex);
            return Response.ok("El rol no existe").build();
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
        return new RoleRecord(roleRepresentation.getId(), roleRepresentation.getName());
    }

    private RoleRepresentation mapRole(Role role) {
        RoleRepresentation roleRep = new RoleRepresentation();
        roleRep.setName(role.getName());
        roleRep.setComposite(Boolean.FALSE);
        roleRep.setDescription(role.getDescription());
        return roleRep;
    }
}
