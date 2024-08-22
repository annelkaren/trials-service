package mx.gob.pjpuebla.trials.core.utils.audit;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;

public class AuditConfigTest {

    @MockBean
    AuditorAware<Jwt> auditorAware;

    @Before
    public void setup() {
        when(auditorAware.getCurrentAuditor())
                .thenReturn(Optional.of(createJwt()));
    }

    private Jwt createJwt() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("kid", RandomStringUtils.random(20));

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", RandomStringUtils.random(20));

        return new Jwt(RandomStringUtils.random(20), Instant.now(), Instant.now().plusMillis(9999), headers, claims);
    }
}
