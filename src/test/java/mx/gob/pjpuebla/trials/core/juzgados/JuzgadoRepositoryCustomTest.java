package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class JuzgadoRepositoryCustomTest extends AuditConfigTest {

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
    private Juzgado juzgado;

    @BeforeEach
    public void setUp() {
        Materia materia = materiaRepository.save(MateriaSetUp.createMateria());
        Distrito distrito = distritoRepository.save(DistritoSetUp.createDistrito());
        Domicilio domicilio = domicilioRepository.save(DomicilioSetUp.createDomicilio());
        Sede sede = SedeSetUp.createSede();
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        sede = sedeRepository.save(sede);
        juzgado = JuzgadoSetUp.createJuzgado(materia, sede);
    }

    @Test
    void getNumeroExpediente() {
        String anioActual = Integer.toString(LocalDate.now().getYear());
        String numExpediente;

        juzgado = juzgadoRepository.save(juzgado);
        juzgadoRepository.generarSecuenciaExpediente(juzgado.getId());

        numExpediente = juzgadoRepository.getNumeroExpediente(juzgado.getId());

        assertThat(numExpediente).matches("\\d{6}[\\/]\\d{4}").endsWith(anioActual);
    }

    @Test
    void reiniciarSecuenciaExpediente() {
        juzgado.setEstado(Estado.ACTIVE);
        juzgado = juzgadoRepository.save(juzgado);
        juzgadoRepository.generarSecuenciaExpediente(juzgado.getId());

        for (Juzgado tmp:juzgadoRepository.findAll()){
            for (int i=0; i<5; i++){
                juzgadoRepository.getNumeroExpediente(tmp.getId());
            }
        }

        assertThat(juzgadoRepository.reiniciarSecuenciasExpedientes()).isTrue();

        for (Juzgado tmp:juzgadoRepository.findAll()){
            String numExpediente = juzgadoRepository.getNumeroExpediente(tmp.getId());
            assertThat(numExpediente).startsWith("000001");
        }
        
    }
}