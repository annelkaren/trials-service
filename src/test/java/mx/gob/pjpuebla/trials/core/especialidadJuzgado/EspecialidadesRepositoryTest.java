package mx.gob.pjpuebla.trials.core.especialidadJuzgado;

import lombok.RequiredArgsConstructor;
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

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@RequiredArgsConstructor
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EspecialidadesRepositoryTest {

    @Autowired
    private EspecialidadesRepository especialidadesRepository;

    @Before

    @After
    public void deleteAll() {
        especialidadesRepository.deleteAll();
    }

    @DisplayName("Should save a Especialidades item with an id greater than 0")
    @Test
    public void save() {
        Especialidades especialidades = createEspecialidades();

        Especialidades entity = especialidadesRepository.save(especialidades);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getId()).isGreaterThan(0);
    }

    @DisplayName("Should get a list with all the saved items of Especialidades")
    @Test
    public void findAll() {
        Especialidades entity1 = createEspecialidades();
        especialidadesRepository.save(entity1);

        Especialidades entity2 = createEspecialidades();
        especialidadesRepository.save(entity2);

        List<Especialidades> expectedList = Arrays.asList(entity1, entity2);
        List<Especialidades> list = especialidadesRepository.findAll();

        Assertions.assertThat(list).isNotNull();
        Assertions.assertThat(list.size()).isEqualTo(expectedList.size());
    }

    private Especialidades createEspecialidades() {
        return Especialidades
                .builder()
                .estado("A")
                .nombre(RandomStringUtils.random(15, true, true))
                .build();
    }

}