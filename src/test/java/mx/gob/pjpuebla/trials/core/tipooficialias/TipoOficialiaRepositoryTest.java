package mx.gob.pjpuebla.trials.core.tipooficialias;

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
class TipoOficialiaRepositoryTest extends AuditConfigTest {

    @Autowired
    private TipoOficialiaRepository tipoOficialiaRepository;

    @BeforeEach
    @AfterEach
    public void deleteAll() {
        tipoOficialiaRepository.deleteAll();
    }

    @DisplayName("Should save a TipoOficialias item with an id greater than 0")
    @Test
    void save() {
        TipoOficialia tipoOficialia = createTipoOficialias();

        TipoOficialia entity = tipoOficialiaRepository.save(tipoOficialia);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getId()).isGreaterThan(0);
    }

    @DisplayName("Should get a list with all the saved items of TipoOficialias")
    @Test
    void findAll() {
        TipoOficialia entity1 = createTipoOficialias();
        tipoOficialiaRepository.save(entity1);

        TipoOficialia entity2 = createTipoOficialias();
        tipoOficialiaRepository.save(entity2);

        List<TipoOficialia> expectedList = Arrays.asList(entity2, entity2);
        List<TipoOficialia> list = tipoOficialiaRepository.findAll();

        Assertions.assertThat(list).isNotNull();
        Assertions.assertThat(list.size()).isEqualTo(expectedList.size());
    }

    private TipoOficialia createTipoOficialias() {
        return TipoOficialia.builder().estado(Estado.ACTIVE).nombre(RandomStringUtils.random(8, true, false)).build();
    }

}