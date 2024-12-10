package mx.gob.pjpuebla.trials.core.recursos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.config.KeycloakSecurityUtil;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.authorization.DecisionEffect;
import org.keycloak.representations.idm.authorization.PolicyEvaluationRequest;
import org.keycloak.representations.idm.authorization.PolicyEvaluationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecursoService {

    private final KeycloakSecurityUtil keycloakSecurityUtil;
    private final AuditorAware<Jwt> auditorAware;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-server-id}")
    private String clientId;

    public List<PolicyRecord> getPermission() {
        List<PolicyRecord> list = new ArrayList<>();
        RealmResource kcRealm = keycloakSecurityUtil.getKeycloakInstance().realm(realm);

        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();
        AuthorizationResource authorizationResource = kcRealm.clients().get(clientId).authorization();

        PolicyEvaluationRequest req = new PolicyEvaluationRequest();
        req.setClientId(clientId);
        req.setUserId(jwt.getSubject());
        PolicyEvaluationResponse evaluate = authorizationResource.policies().evaluate(req);
        List<PolicyEvaluationResponse.EvaluationResultRepresentation> results = evaluate.getResults().stream()
                .filter(res -> res.getStatus().equals(DecisionEffect.PERMIT))
                .toList();
        for (PolicyEvaluationResponse.EvaluationResultRepresentation item : results) {
            String name = item.getResource().getName();
            String permission = name.substring(0, name.indexOf(" with scopes"));
            String scopes = name.substring(name.indexOf("[") + 1, name.indexOf("]"));
            PolicyRecord policyRecord = new PolicyRecord(permission, scopes);
            list.add(policyRecord);
        }
        return list;
    }
}
