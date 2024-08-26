package mx.gob.pjpuebla.trials.core.especialidadJuzgado;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.relJuzgadoEspecialidad.RelJuzgadoEspecialidad;
import mx.gob.pjpuebla.trials.core.relJuzgadoEspecialidad.RelJuzgadoEspecialidadRepository;
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

public class RelJuzgadoEspecialidadRepositoryTest {

    @Autowired
    private RelJuzgadoEspecialidadRepository relJuzgadoEspecialidadRepository;

    @Before

    @After
    public void deleteAll() {
        relJuzgadoEspecialidadRepository.deleteAll();
    }

    @DisplayName("Should save a RelJuzgadoEspecialidad item with an id greater than 0")
    @Test
    public void save() {
        RelJuzgadoEspecialidad relJuzgadoEspecialidad = createRelJuzgadoEspecialidad();

        RelJuzgadoEspecialidad entity = relJuzgadoEspecialidadRepository.save(relJuzgadoEspecialidad);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getId()).isGreaterThan(0);
    }

    @DisplayName("Should get a list with all the saved items of RelJuzgadoEspecialidad")
    @Test
    public void findAll() {
        RelJuzgadoEspecialidad entity1 = createRelJuzgadoEspecialidad();
        relJuzgadoEspecialidadRepository.save(entity1);

        RelJuzgadoEspecialidad entity2 = createRelJuzgadoEspecialidad();
        relJuzgadoEspecialidadRepository.save(entity2);

        List<RelJuzgadoEspecialidad> expectedList = Arrays.asList(entity1, entity2);
        List<RelJuzgadoEspecialidad> list = relJuzgadoEspecialidadRepository.findAll();

        Assertions.assertThat(list).isNotNull();
        Assertions.assertThat(list.size()).isEqualTo(expectedList.size());
    }

    private RelJuzgadoEspecialidad createRelJuzgadoEspecialidad() {
        return RelJuzgadoEspecialidad
                .builder()
                .juzgado((int) (Math.random() * 401) + 100)
                .especialidad((int) (Math.random() * 401) + 100)
                .build();
    }

}