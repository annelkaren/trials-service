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
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialia;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRepository;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaSetUp;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.Tipo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class OficialiaRepositoryTest extends AuditConfigTest {

    @Autowired
    private OficialiaRepository oficialiaRepository;
    @Autowired
    private DomicilioRepository domicilioRepository;
    @Autowired
    private DistritoRepository distritoRepository;
    @Autowired
    private SedeRepository sedeRepository;
    @Autowired
    private TipoOficialiaRepository tipoOficialiaRepository;
    @Autowired
    private JuzgadoRepository juzgadoRepository;
    @Autowired
    private MateriaRepository materiaRepository;

    private Oficialia oficialia;
    private Juzgado juzgado;
    private Materia materia;
    private Domicilio domicilio;
    private Distrito distrito;
    private Sede sede;
    private Sede sede1;

    @BeforeEach
    void setUp() {
        sede = SedeSetUp.createSede();
        TipoOficialia tipoOficialia = TipoOficialiaSetUp.createtipoOficialia();
        tipoOficialia = tipoOficialiaRepository.save(tipoOficialia);
        distrito = distritoRepository.save(DistritoSetUp.createDistrito());
        domicilio = domicilioRepository.save(DomicilioSetUp.createDomicilio());
        materia = materiaRepository.save(MateriaSetUp.createMateria());
        juzgado = JuzgadoSetUp.createJuzgado();

        juzgado.setMateria(materia);
        juzgado.setSede(sede);
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);

        sede = sedeRepository.save(sede);
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

        List<Estado> estados = Arrays.asList(Estado.INACTIVE);
        Optional<OficialiaRecord> entity = oficialiaRepository.findByIdAndEstadoIn(oficialia.getId(), estados);
        assertThat(entity).isPresent();
        assertThat(entity.get().estado()).isEqualTo(Estado.INACTIVE);
    }

    public static Sede createSede() {
        Sede sede = new Sede()
                .setId(4)
                .setVersion(0)
                .setNombre("Sede")
                .setTipo(Tipo.EXTERNO)
                .setEstado(Estado.ACTIVE);
        sede.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return sede;
    }

}
