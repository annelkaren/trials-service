package mx.gob.pjpuebla.trials.core.tipooficialias;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
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
class TipoOficialiasRepositoryTest extends AuditConfigTest {

    @Autowired
    private TipoOficialiasRepository tipoOficialiasRepository;

    @BeforeEach
    @AfterEach
    public void deleteAll() {
        tipoOficialiasRepository.deleteAll();
    }

    @DisplayName("Should save a TipoOficialias item with an id greater than 0")
    @Test
    void save() {
        TipoOficialias tipoOficialias = createTipoOficialias();

        TipoOficialias entity = tipoOficialiasRepository.save(tipoOficialias);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getId()).isGreaterThan(0);
    }

    @DisplayName("Should get a list with all the saved items of TipoOficialias")
    @Test
    void findAll() {
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