package mx.gob.pjpuebla.trials.core.listavalor;

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
public class ListaValorRepositoryTest extends AuditConfigTest {

    @Autowired
    private ListaValorRepository listaValorRepository;

    @Before
    @After
    public void deleteAll() {
        listaValorRepository.deleteAll();
    }

    @DisplayName("Should save a ListaValor item with an id greater than 0")
    @Test
    public void save() {
        ListaValor listaValor = createListaValor();

        ListaValor entity = listaValorRepository.save(listaValor);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isGreaterThan(0);
    }

    @DisplayName("Should get a list with all the saved items of ListaValor")
    @Test
    public void findAll() {
        ListaValor entity1 = createListaValor();
        listaValorRepository.save(entity1);

        ListaValor entity2 = createListaValor();
        listaValorRepository.save(entity2);

        List<ListaValor> expectedList = Arrays.asList(entity2, entity2);
        List<ListaValor> list = listaValorRepository.findAll();

        assertThat(list).isNotNull();
        assertThat(list.size()).isEqualTo(expectedList.size());
    }

    private ListaValor createListaValor() {
        return ListaValor.builder()
                .estado("A")
                .nombre(RandomStringUtils.random(5, true, true))
                .build();
    }
}
