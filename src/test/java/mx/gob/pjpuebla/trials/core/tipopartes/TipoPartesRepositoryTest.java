package mx.gob.pjpuebla.trials.core.tipopartes;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.Estado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesSetUp.createTipoPartes;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class TipoPartesRepositoryTest extends AuditConfigTest {

    @Autowired
    private TipoPartesRepository tipoPartesRepository;
    @Autowired
    private MateriaRepository materiaRepository;
    @Autowired
    private TipoJuicioRepository tipoJuicioRepository;
    @Autowired
    private TipoSistemaRepository tipoSistemaRepository;

    @Test
    void findByAllAndEstadoActive() {
        TipoPartes validTipoPartes = createTipoPartes();

        TipoSistema tipoSistema = TipoSistemaSetUp.createTipoSistema();
        tipoSistema = tipoSistemaRepository.save(tipoSistema);

        Materia materia = MateriaSetUp.createMateria();
        materia = materiaRepository.save(materia);

        TipoJuicio tipoJuicio = TipoJuicioSetUp.createTipoJuicio(tipoSistema, materia);
        tipoJuicio = tipoJuicioRepository.save(tipoJuicio);
        validTipoPartes.setTipoJuicio(tipoJuicio);

        tipoPartesRepository.save(validTipoPartes);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<TipoPartes> page = tipoPartesRepository.findAll(Example.of(new TipoPartes().setNombre("A").setEstado(Estado.ACTIVE), exampleMatcher),PageRequest.of(0, 20));
        assertThat(page.get()).hasSize(1);

    }

    @Test
    void findByIdAndEstadoActive() {
        TipoPartes validTipoPartes = createTipoPartes();

        TipoSistema tipoSistema = TipoSistemaSetUp.createTipoSistema();
        tipoSistema = tipoSistemaRepository.save(tipoSistema);

        Materia materia = MateriaSetUp.createMateria();
        materia = materiaRepository.save(materia);

        TipoJuicio tipoJuicio = TipoJuicioSetUp.createTipoJuicio(tipoSistema, materia);
        tipoJuicio = tipoJuicioRepository.save(tipoJuicio);
        validTipoPartes.setTipoJuicio(tipoJuicio);

        TipoPartes entity = tipoPartesRepository.save(validTipoPartes);

        Optional<TipoPartes> tipoPartes = tipoPartesRepository.findByIdAndEstado(entity.getId(), Estado.ACTIVE);
        assertThat(tipoPartes).isPresent();
        assertThat(tipoPartes.get().getEstado()).isEqualTo(Estado.ACTIVE);
    }

}
