package mx.gob.pjpuebla.trials.workflow.carpeta;

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
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Disabled
class CarpetaRepositoryTest extends AuditConfigTest {

    @Autowired
    private CarpetaRepository carpetaRepository;

    @Autowired
    private JuzgadoRepository juzgadoRepository;

    @Autowired
    private TipoJuicioRepository tipoJuicioRepository;

    @Autowired
    private TipoSistemaRepository tipoSistemaRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private SedeRepository sedeRepository;

    @Autowired
    private DistritoRepository distritoRepository;

    @Autowired
    private DomicilioRepository domicilioRepository;

    @Test
    void findByExpedienteAndJuzgadoId() {
        TipoSistema tipoSistema = tipoSistemaRepository.save(TipoSistemaSetUp.createTipoSistema());
        Materia materia = materiaRepository.save(MateriaSetUp.createMateria());
        TipoJuicio tipoJuicio = tipoJuicioRepository.save(TipoJuicioSetUp.createTipoJuicio(tipoSistema, materia).setVersion(1));
        Distrito distrito = distritoRepository.save(DistritoSetUp.createDistrito());
        Domicilio domicilio = domicilioRepository.save(DomicilioSetUp.createDomicilio());
        Sede sede = sedeRepository.save(SedeSetUp.createSede().setDistrito(distrito).setDomicilio(domicilio));
        Juzgado juzgado = juzgadoRepository.save(JuzgadoSetUp.createJuzgado().setMateria(materia).setSede(sede).setTipoJuicios(Collections.singletonList(tipoJuicio)));
        Carpeta carpeta = carpetaRepository.save(CarpetaSetUp.create(tipoJuicio, juzgado).setTipoCarpeta(TipoCarpeta.DEMANDA));

        Optional<Carpeta> entity = carpetaRepository.findByExpedienteAndJuzgadoId(carpeta.getExpediente(), carpeta.getJuzgado().getId());

        assertThat(entity).isPresent();
        assertThat(entity.get().getId()).isEqualTo(carpeta.getId());
    }

}