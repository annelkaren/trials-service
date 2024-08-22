package mx.gob.pjpuebla.trials.core.tiposistema;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
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
public class TipoSistemaRepositoryTest extends AuditConfigTest {

    @Autowired
    private TipoSistemaRepository tipoSistemaRepository;

    @Before
    @After
    public void deleteAll() {
        tipoSistemaRepository.deleteAll();
    }

    @DisplayName("Should save a Tipo Sistema item with an id greater than 0")
    @Test
    public void save() {
        TipoSistema tipoSistema = createTipoSistema();

        TipoSistema entity = tipoSistemaRepository.save(tipoSistema);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getId()).isGreaterThan(0);
    }

    @DisplayName("Should get a list with all the saved items of Tipo Sistema")
    @Test
    public void findAll() {
        TipoSistema entity1 = createTipoSistema();
        tipoSistemaRepository.save(entity1);

        TipoSistema entity2 = createTipoSistema();
        tipoSistemaRepository.save(entity2);

        List<TipoSistema> expectedList = Arrays.asList(entity2, entity2);
        List<TipoSistema> list = tipoSistemaRepository.findAll();

        Assertions.assertThat(list).isNotNull();
        Assertions.assertThat(list.size()).isEqualTo(expectedList.size());
    }

    private TipoSistema createTipoSistema() {
        return TipoSistema.builder().estado("A").nombre(RandomStringUtils.random(8, true, false)).build();
    }


}
