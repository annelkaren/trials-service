package mx.gob.pjpuebla.trials.workflow.folios;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFoliosSetUp.createJuzgadoFolios;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {"spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Disabled
class JuzgadoFoliosRepositoryTest extends AuditConfigTest {
    @Autowired
    private JuzgadoFoliosRepository juzgadoFoliosRepository;
    @Autowired
    private JuzgadoRepository juzgadoRepository;
    @Autowired
    private SedeRepository sedeRepository;
    @Autowired
    private MateriaRepository materiaRepository;
    @Autowired
    private DistritoRepository distritoRepository;
    @Autowired
    private DomicilioRepository domicilioRepository;
    @Autowired
    private TipoSistemaRepository tipoSistemaRepository;

    private JuzgadoFolios juzgadoFolios;

    @BeforeEach
    public void setUp() {
        Materia materia = materiaRepository.save(MateriaSetUp.createMateria());
        Distrito distrito = distritoRepository.save(DistritoSetUp.createDistrito());
        Domicilio domicilio = domicilioRepository.save(DomicilioSetUp.createDomicilio());
        TipoSistema tipoSistema = tipoSistemaRepository.save(TipoSistemaSetUp.createTipoSistema());
        Sede sede = SedeSetUp.createSede();
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        sede = sedeRepository.save(sede);
        TipoJuicio tj1 = TipoJuicioSetUp.createTipoJuicio(tipoSistema, materia);
        TipoJuicio tj2 = TipoJuicioSetUp.createTipoJuicio(tipoSistema, materia).setId(2).setNombre("Laboral Dos");
        Juzgado juzgado = JuzgadoSetUp.createJuzgado(materia, sede)
                .setTipoJuicios(Arrays.asList(tj1, tj2));

        juzgado = juzgadoRepository.save(juzgado);

        juzgadoFolios = createJuzgadoFolios();
        juzgadoFolios.setJuzgado(juzgado);
        juzgadoFolios = juzgadoFoliosRepository.save(juzgadoFolios);
    }

    @Test
    void findByAllAndEstadoActive() {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("tipoDocumento", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("value", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("year", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        List<JuzgadoFolios> juzgadoFoliosList = juzgadoFoliosRepository.findAll(Example.of(
                new JuzgadoFolios().setTipoCarpeta(TipoCarpeta.DEMANDA).setValue(1).setYear(2024), exampleMatcher));

        assertThat(juzgadoFoliosList).hasSize(1);
    }

    @Test
    void findByIdAndEstadoActive() {
        Optional<JuzgadoFolios> juzgadoFoliosResult = juzgadoFoliosRepository.findByJuzgadoAndTipoCarpeta(juzgadoFolios.getJuzgado(), TipoCarpeta.DEMANDA);
        assertThat(juzgadoFoliosResult).isPresent();
        assertThat(juzgadoFoliosResult.get().getTipoCarpeta()).isEqualTo(TipoCarpeta.DEMANDA);
    }
}