package mx.gob.pjpuebla.trials.core.tipopartes;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
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
public class TipoPartesRepositoryTest extends AuditConfigTest {

    @Autowired
    private TipoPartesRepository tipopartesRepository;

    @Before
    @After
    public void deleteAll() {
        tipopartesRepository.deleteAll();
    }

    @DisplayName("Should save a TipoPartes item with an id greater than 0")
    @Test
    public void save() {
        TipoPartes tipopartes = createTipoPartes();

        TipoPartes entity = tipopartesRepository.save(tipopartes);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isPositive();
    }

    @DisplayName("Should get a list with all the saved items of TipoPartes")
    @Test
    public void findAll() {
        TipoPartes entity1 = createTipoPartes();
        tipopartesRepository.save(entity1);

        TipoPartes entity2 = createTipoPartes();
        tipopartesRepository.save(entity2);

        List<TipoPartes> expectedList = Arrays.asList(entity2, entity2);
        List<TipoPartes> list = tipopartesRepository.findAll();

        assertThat(list)
                .isNotNull()
                .hasSameSizeAs(expectedList);
    }

    private TipoPartes createTipoPartes() {
        return new TipoPartes()
                .setEstado("A")
                .setNombre(RandomStringUtils.random(5, true, true));
    }
}