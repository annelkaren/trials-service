package mx.gob.pjpuebla.trials.core.organismos;


import org.assertj.core.api.Assertions;
import lombok.RequiredArgsConstructor;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.After;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@RequiredArgsConstructor
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrganismosRepositoryTest extends AuditConfigTest{
    @Autowired
    private OrganismoRepository organismosRepository;

    @Before

    @After
    public void deleteAll() {
        organismosRepository.deleteAll();
    }

    @DisplayName("Should save a Organismo item with an id greater than 0")
    @Test
    public void save() {
        Organismo organismo = createOrganismo();

        Organismo entity = organismosRepository.save(organismo);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getId()).isGreaterThan(0);
    }

    @DisplayName("Should get a list with all the saved items of Organismos")
    @Test
    public void findAll() {
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
        return Organismo.builder().estado("A").nombre(RandomStringUtils.random(8, true, false)).build();
    }

}
