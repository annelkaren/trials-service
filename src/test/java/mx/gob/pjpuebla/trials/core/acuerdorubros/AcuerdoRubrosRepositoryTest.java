package mx.gob.pjpuebla.trials.core.acuerdorubros;

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
        "/scripts/INSERT_ACUERDO_RUBRO.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_ACUERDO_RUBRO.sql",
        "/scripts/DELETE_MATERIAS.sql",
        "/scripts/DELETE_TIPO_SISTEMAS.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class AcuerdoRubrosRepositoryTest extends AuditConfigTest {

    @Autowired
    private AcuerdoRubrosRepository acuerdoRubrosRepository;

    @Test
    void testFindByMateria() {
        Materia materia = MateriaSetUp.createMateria();
        materia.setId(100);
        Page<AcuerdoRubros> rubros = acuerdoRubrosRepository.findByMateria(materia, PageRequest.of(0, 10));
        assertThat(rubros.getContent()).isNotEmpty();
    }

    @Test
    void testFindByMateriaAndTipoSistema() {
        Materia materia = MateriaSetUp.createMateria();
        materia.setId(250);
        materia.setNombre("FAMILAR");
        TipoSistema tipoSistema = TipoSistemaSetUp.createTipoSistema();
        tipoSistema.setId(100);
        Page<AcuerdoRubros> rubros = acuerdoRubrosRepository.findByMateriaAndTipoSistema(materia, tipoSistema, PageRequest.of(0, 10));
        assertThat(rubros.getContent()).isNotEmpty();
    }
}