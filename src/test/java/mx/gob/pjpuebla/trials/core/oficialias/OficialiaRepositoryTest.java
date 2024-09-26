package mx.gob.pjpuebla.trials.core.oficialias;

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
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialia;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRepository;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class OficialiaRepositoryTest extends AuditConfigTest {

    @Autowired
    private OficialiaRepository oficialiaRepository;
    @Autowired
    private DomicilioRepository domicilioRepository;
    @Autowired
    private DistritoRepository distritoRepository;
    @Autowired
    private SedeRepository sedeRepository;
    @Autowired
    private JuzgadoRepository juzgadoRepository;
    @Autowired
    private MateriaRepository materiaRepository;
    @Autowired
    private TipoJuicioRepository tipoJuicioRepository;
    @Autowired
    private TipoSistemaRepository tipoSistemaRepository;

    @Autowired
    private TipoOficialiaRepository tipoOficialiaRepository;
    private Oficialia oficialia;

    @BeforeEach
    void setUp() {
        TipoOficialia tipoOficialia = TipoOficialiaSetUp.createtipoOficialia();
        Materia materia = MateriaSetUp.createMateria();
        materia = materiaRepository.save(materia);
        tipoOficialia = tipoOficialiaRepository.save(tipoOficialia);
        Distrito distrito = distritoRepository.save(DistritoSetUp.createDistrito());
        Domicilio domicilio = domicilioRepository.save(DomicilioSetUp.createDomicilio());

        TipoSistema tipoSistema = tipoSistemaRepository.save(TipoSistemaSetUp.createTipoSistema());
        TipoJuicio tipoJuicio = tipoJuicioRepository.save(TipoJuicioSetUp.createTipoJuicio(tipoSistema, materia));

        Sede sede = SedeSetUp.createSede();
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        sede = sedeRepository.save(sede);
        Juzgado juzgado = JuzgadoSetUp.createJuzgado(materia,sede);
        juzgado.setTipoJuicios(List.of(tipoJuicio));
        juzgado = juzgadoRepository.save(juzgado);
        oficialia = OficialiaSetUp.createOficialia(tipoOficialia, sede);
        oficialia.setJuzgado(juzgado);
    }

    @Test
    void findByIdAndEstadoActive() {
        oficialia = oficialiaRepository.save(oficialia);
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Optional<OficialiaRecord> entity = oficialiaRepository.findByIdAndEstadoIn(oficialia.getId(), estados);
        assertThat(entity).isPresent();
        assertThat(entity.get().estado()).isEqualTo(Estado.ACTIVE);
    }

    @Test
    void findByIdAndEstadoInactive() {
        oficialia.setEstado(Estado.INACTIVE);
        oficialia = oficialiaRepository.save(oficialia);
        List<Estado> estados = Arrays.asList(Estado.INACTIVE);
        Optional<OficialiaRecord> entity = oficialiaRepository.findByIdAndEstadoIn(oficialia.getId(), estados);
        assertThat(entity).isPresent();
        assertThat(entity.get().estado()).isEqualTo(Estado.INACTIVE);
    }

    @Test
    void findOficialiasComunes(){
        oficialia = oficialiaRepository.save(oficialia);

        List<Oficialia> oficialiasComunes = oficialiaRepository.findOficialiaComun();

        assertThat(oficialiasComunes).isNotEmpty().anyMatch(ofi -> ofi.getTipoOficialia().getNombre()=="Común");
    }
}
