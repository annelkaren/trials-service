package mx.gob.pjpuebla.trials.core.recursos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.config.KeycloakSecurityUtil;
import mx.gob.pjpuebla.trials.core.recursos.menu.Menu;
import mx.gob.pjpuebla.trials.core.recursos.menu.Node;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.authorization.DecisionEffect;
import org.keycloak.representations.idm.authorization.PolicyEvaluationRequest;
import org.keycloak.representations.idm.authorization.PolicyEvaluationResponse;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecursoService {

    private final KeycloakSecurityUtil keycloakSecurityUtil;
    private final AuditorAware<Jwt> auditorAware;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-server-id}")
    private String clientId;

    public Node getMenu() {
        RealmResource kcRealm = keycloakSecurityUtil.getKeycloakInstance().realm(realm);

        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();
        AuthorizationResource authorizationResource = kcRealm.clients().get(clientId).authorization();

        PolicyEvaluationRequest req = new PolicyEvaluationRequest();
        req.setClientId(clientId);
        req.setUserId(jwt.getSubject());
        PolicyEvaluationResponse evaluate = authorizationResource.policies().evaluate(req);
        List<PolicyEvaluationResponse.EvaluationResultRepresentation> results = evaluate.getResults().stream()
                .filter(res -> res.getStatus().equals(DecisionEffect.PERMIT))//filtering for allowed results
                .toList();

        List<ResourceRepresentation> resources = authorizationResource.resources().resources();//TODO Annel, hacer esta lista Cacheable

        Set<String> uris = resources.stream()
                .filter(r -> r.getAttributes().containsKey("menu"))
                .filter(r -> r.getAttributes().get(("menu")).contains("true"))
                .filter(r -> {
                    for (PolicyEvaluationResponse.EvaluationResultRepresentation result : Collections.unmodifiableList(results)) {
                        if (result.getResource().getId().equalsIgnoreCase(r.getId())) {
                            return true;
                        }
                    }
                    return false;
                }).flatMap(r -> modifyUrl(r.getDisplayName(),  r.getAttributes() ,r.getUris()).stream())
                .collect(Collectors.toSet());

        return Menu.parseToMenu(uris);

    }

    private Set<String> modifyUrl(String displayName, Map<String, List<String>> attributes, Set<String> uris) {
        Set<String> newHashSet = new HashSet<>();
        for (String uri : uris) {
            newHashSet.add(uri + "--" + displayName );
        }
        return newHashSet;
    }
}
