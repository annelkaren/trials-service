package mx.gob.pjpuebla.trials.core.tipoOficialias;

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
public class TipoOficialiasRepositoryTest extends AuditConfigTest {

    @Autowired
    private TipoOficialiasRepository tipoOficialiasRepository;

    @Before

    @After
    public void deleteAll() {
        tipoOficialiasRepository.deleteAll();
    }

    @DisplayName("Should save a TipoOficialias item with an id greater than 0")
    @Test
    public void save() {
        TipoOficialias tipoOficialias = createTipoOficialias();

        TipoOficialias entity = tipoOficialiasRepository.save(tipoOficialias);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getId()).isGreaterThan(0);
    }

    @DisplayName("Should get a list with all the saved items of TipoOficialias")
    @Test
    public void findAll() {
        TipoOficialias entity1 = createTipoOficialias();
        tipoOficialiasRepository.save(entity1);

        TipoOficialias entity2 = createTipoOficialias();
        tipoOficialiasRepository.save(entity2);

        List<TipoOficialias> expectedList = Arrays.asList(entity2, entity2);
        List<TipoOficialias> list = tipoOficialiasRepository.findAll();

        Assertions.assertThat(list).isNotNull();
        Assertions.assertThat(list.size()).isEqualTo(expectedList.size());
    }

    private TipoOficialias createTipoOficialias() {
        return TipoOficialias.builder().estado("A").nombre(RandomStringUtils.random(8, true, false)).build();
    }

}