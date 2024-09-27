package mx.gob.pjpuebla.trials.core.listavalor;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
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
@Disabled
class ListaValorRepositoryTest extends AuditConfigTest {

    @Autowired
    private ListaValorRepository listaValorRepository;

    @BeforeEach
    @AfterEach
    public void deleteAll() {
        listaValorRepository.deleteAll();
    }

    @DisplayName("Should save a ListaValor item with an id greater than 0")
    @Test
    void save() {
        ListaValor listaValor = createListaValor();

        ListaValor entity = listaValorRepository.save(listaValor);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isPositive();
    }

    @DisplayName("Should get a list with all the saved items of ListaValor")
    @Test
    void findAll() {
        ListaValor entity1 = createListaValor();
        listaValorRepository.save(entity1);

        ListaValor entity2 = createListaValor();
        listaValorRepository.save(entity2);

        List<ListaValor> expectedList = Arrays.asList(entity2, entity2);
        List<ListaValor> list = listaValorRepository.findAll();

        assertThat(list)
                .isNotNull()
                .hasSameSizeAs(expectedList);
    }

    private ListaValor createListaValor() {
        return ListaValor.builder()
                .estado("A")
                .nombre(RandomStringUtils.random(5, true, true))
                .build();
    }
}
