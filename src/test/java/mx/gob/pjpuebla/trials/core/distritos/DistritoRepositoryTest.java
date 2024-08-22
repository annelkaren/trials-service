package mx.gob.pjpuebla.trials.core.distritos;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DistritoRepositoryTest extends AuditConfigTest {

    @Autowired
    private DistritoRepository distritoRepository;

    @Before
    @After
    public void deleteAll() {
        distritoRepository.deleteAll();
    }

    @Test
    public void findAll() {
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
