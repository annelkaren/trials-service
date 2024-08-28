package mx.gob.pjpuebla.trials.core.tipopartes;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.Estado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesSetUp.createTipoPartes;
import static mx.gob.pjpuebla.trials.core.materias.MateriaSetUp.createMateria;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TipoPartesRepositoryTest extends AuditConfigTest {

    @Autowired
    private TipoPartesRepository tipoPartesRepository;
    //@Autowired
    //private MateriaRepository materiaRepository;

    @Test
    void findById(){
        tipoPartesRepository.save(createTipoPartes());
        Optional<TipoPartes> tipoPartes = tipoPartesRepository.findById(1);
        assertThat(tipoPartes).isPresent();
    }

    /*@Test
    void findByAllAndEstadoActive() {
        TipoPartes validTipoPartes = createTipoPartes();
        Materia validMateria = createMateria();

        validTipoPartes.setMateria(materiaRepository.save(validMateria));
        tipoPartesRepository.save(validTipoPartes);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<TipoPartes> page = tipoPartesRepository.findAll(PageRequest.of(0, 20));
        assertThat(page.get()).hasSize(1);
    }
    */


    /*
    @Test
    void findByIdAndEstadoActive() {
        TipoPartes validTipoPartes = createTipoPartes();
        Materia validMateria = createMateria();
        validTipoPartes.setMateria(materiaRepository.save(validMateria));

        tipoPartesRepository.save(validTipoPartes);
        Optional<TipoPartes> tipoPartes = tipoPartesRepository.findById(1);
        assertThat(tipoPartes).isPresent();
        assertThat(tipoPartes.get().getEstado()).isEqualTo(Estado.ACTIVE);
    }*/

}
