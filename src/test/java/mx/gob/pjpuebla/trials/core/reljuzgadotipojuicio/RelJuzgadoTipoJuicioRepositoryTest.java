package mx.gob.pjpuebla.trials.core.reljuzgadotipojuicio;

import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
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
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp.createTipoJuicio;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {"spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop", "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class RelJuzgadoTipoJuicioRepositoryTest extends AuditConfigTest {
    @Autowired
    private RelJuzgadoTipoJuicioRepository repository;
    @Autowired
    private TipoJuicioRepository tipoJuicioRepository;
    @Autowired
    private JuzgadoRepository juzgadoRepository;
    @Autowired
    private MateriaRepository materiaRepository;
    @Autowired
    private TipoSistemaRepository tipoSistemaRepository;
    @Autowired
    private SedeRepository sedeRepository;
    @Autowired
    private DistritoRepository distritoRepository;
    @Autowired
    private DomicilioRepository domicilioRepository;

    private Juzgado juzgado;
    private TipoJuicio tipoJuicio;

    @BeforeEach
    void setUp() {
        Materia materia = MateriaSetUp.createMateria();
        materiaRepository.save(materia);

        Sede sede = SedeSetUp.createSede();
        sede.setDomicilio(domicilioRepository.save(DomicilioSetUp.createDomicilio()));
        sede.setDistrito(distritoRepository.save(DistritoSetUp.createDistrito()));
        sedeRepository.save(sede);

        juzgado = JuzgadoSetUp.createJuzgado(materia, sede);
        juzgadoRepository.save(juzgado);

        TipoSistema tipoSistema = TipoSistemaSetUp.createTipoSistema();
        tipoSistemaRepository.save(tipoSistema);

        tipoJuicio = createTipoJuicio(tipoSistema, materia);
        tipoJuicioRepository.save(tipoJuicio);

        RelJuzgadoTipoJuicio relJuzgadoTipoJuicio = new RelJuzgadoTipoJuicio();
        relJuzgadoTipoJuicio.setJuzgado(juzgado);
        relJuzgadoTipoJuicio.setTipoJuicio(tipoJuicio);
        repository.save(relJuzgadoTipoJuicio);

    }

    @Test
    void findAllByjuzgado() {
        List<RelJuzgadoTipoJuicio> result = repository.findAllByjuzgado(juzgado);

        assertThat(result).hasSizeGreaterThan(0);
        assertThat(result.get(0).getJuzgado()).isEqualTo(juzgado);
        assertThat(result.get(0).getTipoJuicio()).isEqualTo(tipoJuicio);
    }
}