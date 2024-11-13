package mx.gob.pjpuebla.trials.core.tipoaudiencia;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@Sql(value = {
        "/scripts/INSERT_MATERIAS.sql",
        "/scripts/INSERT_TIPO_SISTEMAS.sql",
        "/scripts/INSERT_TIPO_AUDIENCIA.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_TIPO_AUDIENCIA.sql",
        "/scripts/DELETE_MATERIAS.sql",
        "/scripts/DELETE_TIPO_SISTEMAS.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class TipoAudienciaRepositoryTest extends AuditConfigTest {

    @Autowired
    private TipoAudienciaRepository tipoAudienciaRepository;

    @Test
    void testFindByNombre() {
        TipoAudiencia tipoAudiencia = tipoAudienciaRepository.findByNombre("Junta de avenencia y contestación de demanda");
        assertThat(tipoAudiencia).isNotNull();
        assertThat(tipoAudiencia.getNombre()).isEqualTo("Junta de avenencia y contestación de demanda");
    }

    @Test
    void testFindByMateria() {
        Materia materia = MateriaSetUp.createMateria();
        materia.setId(200);
        Page<TipoAudiencia> tipoAudiencia = tipoAudienciaRepository.findByMateriaAndNombreContainingIgnoreCase(materia, "Conciliación", PageRequest.of(0, 10));
        assertThat(tipoAudiencia.getContent()).isNotEmpty();
    }

    @Test
    void testFindByMateriaAndTipoSistema() {
        Materia materia = MateriaSetUp.createMateria();
        materia.setId(250);
        TipoSistema tipoSistema = TipoSistemaSetUp.createTipoSistema();
        tipoSistema.setId(101);
        Page<TipoAudiencia> tipoAudiencia = tipoAudienciaRepository.findByMateriaAndTipoSistemaAndNombreContainingIgnoreCase(materia, tipoSistema, "Aprobación de Convenio", PageRequest.of(0, 10));
        assertThat(tipoAudiencia.getContent()).isNotEmpty();
    }

}