package mx.gob.pjpuebla.trials.core.estadoCivil;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;


@RunWith(SpringRunner.class)
@DataJpaTest
@RequiredArgsConstructor
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EstadoCivilRepositoryTest extends AuditConfigTest {

    @Autowired
    private EstadoCivilRepository estadoCivilRepository;

    @Before

    @After
    public void deleteAll() {
        estadoCivilRepository.deleteAll();
    }

    @DisplayName("Should save a EstadoCivil item with an id greater than 0")
    @Test
    public void save() {
        EstadoCivil estadoCivil = createEstadoCivil();

        EstadoCivil entity = estadoCivilRepository.save(estadoCivil);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getId()).isGreaterThan(0);
    }

    @DisplayName("Should get a list with all the saved items of EstadoCivil")
    @Test
    public void findAll() {
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
                .estado("A")
                .nombre(lista.get(0))
                .build();
    }

}