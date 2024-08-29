package mx.gob.pjpuebla.trials.core.tiposistema;

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
class TipoSistemaRepositoryTest extends AuditConfigTest {

    @Autowired
    private TipoSistemaRepository tipoSistemaRepository;

    @BeforeEach
    @AfterEach
    public void deleteAll() {
        tipoSistemaRepository.deleteAll();
    }

    @DisplayName("Should save a Tipo Sistema item with an id greater than 0")
    @Test
    void save() {
        TipoSistema tipoSistema = createTipoSistema();

        TipoSistema entity = tipoSistemaRepository.save(tipoSistema);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getId()).isGreaterThan(0);
    }

    @DisplayName("Should get a list with all the saved items of Tipo Sistema")
    @Test
    void findAll() {
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
        return TipoSistema.builder().estado(Estado.ACTIVE).nombre(RandomStringUtils.random(8, true, false)).build();
    }


}
