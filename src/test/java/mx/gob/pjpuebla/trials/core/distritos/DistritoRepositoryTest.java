package mx.gob.pjpuebla.trials.core.distritos;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class DistritoRepositoryTest extends AuditConfigTest {

    @Autowired
    private DistritoRepository distritoRepository;

    @BeforeEach
    @AfterEach
    public void deleteAll() {
        distritoRepository.deleteAll();
    }

    @Test
    void findAll() {
        Distrito entity1 = createDistrito();
        distritoRepository.save(entity1);

        Distrito entity2 = createDistrito();
        distritoRepository.save(entity2);

        List<Distrito> expectedList = Arrays.asList(entity1, entity2);
        List<Distrito> list = distritoRepository.findAll();

        assertThat(list)
                .isNotNull()
                .hasSameSizeAs(expectedList);
    }

    private Distrito createDistrito() {
        return Distrito.builder()
                .estado("A")
                .region("Sur")
                .nombre(RandomStringUtils.random(5, true, true))
                .build();
    }
}
