package mx.gob.pjpuebla.trials.core.estadocivil;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.Estado;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class EstadoCivilRepositoryTest extends AuditConfigTest {

    @Autowired
    private EstadoCivilRepository estadoCivilRepository;

    @BeforeEach
    @AfterEach
    public void deleteAll() {
        estadoCivilRepository.deleteAll();
    }

    @DisplayName("Should save a EstadoCivil item with an id greater than 0")
    @Test
    void save() {
        EstadoCivil estadoCivil = createEstadoCivil();

        EstadoCivil entity = estadoCivilRepository.save(estadoCivil);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getId()).isGreaterThan(0);
    }

    @DisplayName("Should get a list with all the saved items of EstadoCivil")
    @Test
    void findAll() {
        EstadoCivil entity1 = createEstadoCivil();
        estadoCivilRepository.save(entity1);

        EstadoCivil entity2 = createEstadoCivil();
        estadoCivilRepository.save(entity2);

        List<EstadoCivil> expectedList = Arrays.asList(entity1, entity2);
        List<EstadoCivil> list = estadoCivilRepository.findAll();

        Assertions.assertThat(list).isNotNull();
        Assertions.assertThat(list.size()).isEqualTo(expectedList.size());
    }

    private EstadoCivil createEstadoCivil() {
        List<String> lista = new ArrayList<>();
        lista.add("Casado/a");
        lista.add("Soltero/a");
        lista.add("Divorciado/a");
        lista.add("Viudo/a");
        lista.add("Separado/a en Proceso Judicial");
        lista.add("Concubinato");
        Collections.shuffle(lista);

        return EstadoCivil
                .builder()
                .estado(Estado.ACTIVE) // estado("A")
                .nombre(lista.get(0))
                .build();
    }

}