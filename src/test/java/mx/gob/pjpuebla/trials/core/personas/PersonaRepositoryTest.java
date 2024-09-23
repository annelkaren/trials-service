package mx.gob.pjpuebla.trials.core.personas;

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
class PersonaRepositoryTest extends AuditConfigTest {

    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private DomicilioRepository domicilioRepository;
    @Autowired
    private SedeRepository sedeRepository;
    @Autowired
    private MateriaRepository materiaRepository;
    @Autowired
    private DistritoRepository distritoRepository;
    @Autowired
    private JuzgadoRepository juzgadoRepository;
    private Persona persona = PersonaSetUp.createPersona();

    @BeforeEach
    void setUp() {
        Domicilio domicilio = domicilioRepository.save(DomicilioSetUp.createDomicilio());
        persona.setDomicilio(domicilio);
    }

    @Test
    void findByIdAndEstadoActive() {
        persona = personaRepository.save(persona);
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Optional<PersonaRecord> entity = personaRepository.findByIdAndEstadoIn(persona.getId(), estados);
        assertThat(entity).isPresent();
        assertThat(entity.get().estado()).isEqualTo(Estado.ACTIVE);
    }

    @Test
    void findByIdAndEstadoInactive() {
        persona.setEstado(Estado.INACTIVE);
        persona = personaRepository.save(persona);
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Optional<PersonaRecord> entity = personaRepository.findByIdAndEstadoIn(persona.getId(), estados);
        assertThat(entity).isPresent();
        assertThat(entity.get().estado()).isEqualTo(Estado.INACTIVE);
    }

    @Test
    void findByCurp() {
        persona = personaRepository.save(persona);
        Optional<PersonaRecord> entity = personaRepository.findByCurp(persona.getCurp());
        assertThat(entity).isPresent();
    }

    @Test
    void findByUsuario() {
        persona.setUsuario("6b13785f-d213-4585-a76b-437ffe57c9c7");
        persona = personaRepository.save(persona);
        Optional<Persona> entity = personaRepository.findByUsuario(persona.getUsuario());
        assertThat(entity).isPresent();
        assertThat(entity.get().getNombre()).isEqualTo(persona.getNombre());
    }

    @Test
    void findByUsuarioAndJuzgadoIdAndEstadoIn() {
        Materia materia = materiaRepository.save(MateriaSetUp.createMateria());
        Distrito distrito = distritoRepository.save(DistritoSetUp.createDistrito());
        Domicilio domicilio = domicilioRepository.save(DomicilioSetUp.createDomicilio());
        Sede sede = SedeSetUp.createSede();
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        sede = sedeRepository.save(sede);
        TipoSistema tipoSistema = TipoSistemaSetUp.createTipoSistema();
        TipoJuicio tipoJuicio = TipoJuicioSetUp.createTipoJuicio(tipoSistema, materia);
        Juzgado juzgado = juzgadoRepository.save(JuzgadoSetUp.createJuzgado(materia, sede)
                .setTipoJuicios(List.of(tipoJuicio)));
        persona.setUsuario("6b13785f-d213-4585-a76b-437ffe57c9c7");
        persona.setJuzgado(juzgado);
        persona = personaRepository.save(persona);
        Optional<Persona> entity = personaRepository
                .findByUsuarioAndJuzgadoIdAndEstadoIn(persona.getUsuario(), persona.getJuzgado().getId(), List.of(Estado.ACTIVE));
        assertThat(entity).isPresent();
        assertThat(entity.get().getNombre()).isEqualTo(persona.getNombre());
        assertThat(entity.get().getUsuario()).isEqualTo(persona.getUsuario());
    }
}