package mx.gob.pjpuebla.trials.core.organismos;


import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.Estado;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class OrganismoRepositoryTest extends AuditConfigTest {
    @Autowired
    private OrganismoRepository organismosRepository;

    @BeforeEach
    @AfterEach
    public void deleteAll() {
        organismosRepository.deleteAll();
    }

    @DisplayName("Should save a Organismo item with an id greater than 0")
    @Test
    void save() {
        Organismo organismo = createOrganismo();

        Organismo entity = organismosRepository.save(organismo);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getId()).isGreaterThan(0);
    }

    @DisplayName("Should get a list with all the saved items of Organismos")
    @Test
    void findAll() {
        Organismo entity1 = createOrganismo();
        organismosRepository.save(entity1);

        Organismo entity2 = createOrganismo();
        organismosRepository.save(entity2);

        List<Organismo> expectedList = Arrays.asList(entity2, entity2);
        List<Organismo> list = organismosRepository.findAll();

        Assertions.assertThat(list).isNotNull();
        Assertions.assertThat(list.size()).isEqualTo(expectedList.size());
    }

    private Organismo createOrganismo() {
        return Organismo.builder().estado(Estado.ACTIVE).nombre(RandomStringUtils.random(8, true, false)).build();
    }

}
