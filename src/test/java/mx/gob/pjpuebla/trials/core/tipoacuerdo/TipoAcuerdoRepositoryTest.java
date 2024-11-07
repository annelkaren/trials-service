package mx.gob.pjpuebla.trials.core.tipoacuerdo;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest(properties = {"spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(value = {
        "/scripts/INSERT_MATERIAS.sql",
        "/scripts/INSERT_TIPO_SISTEMAS.sql",
        "/scripts/INSERT_TIPO_ACUERDO.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_TIPO_ACUERDO.sql",
        "/scripts/DELETE_MATERIAS.sql",
        "/scripts/DELETE_TIPO_SISTEMAS.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class TipoAcuerdoRepositoryTest extends AuditConfigTest {

    @Autowired
    private TipoAcuerdoRepository tipoAcuerdoRepository;

    @Test
    void findByMateriaId() {

        Stream.of(100, 150, 200, 300).forEach(materiaId -> {
            List<TipoAcuerdo> tipoAcuerdos = tipoAcuerdoRepository.findByMateriaId(materiaId);
            assertNotNull(tipoAcuerdos, "La lista no debe ser nula para materiaId " + materiaId);
            assertFalse(tipoAcuerdos.isEmpty(), "La lista no debe estar vacía para materiaId " + materiaId);
            assertTrue(tipoAcuerdos.stream().allMatch(t -> t.getMateria().getId().equals(materiaId)),
                    "Todos los TipoAcuerdo deben tener el materiaId esperado para materiaId " + materiaId);
        });
    }

    @Test
    void findByMateriaIdAndTipoSistemaId() {
        Materia materia = MateriaSetUp.createMateria();
        materia.setId(250);

        Stream.of(100, 101).forEach(tipoSistemaId -> {
            List<TipoAcuerdo> tipoAcuerdos = tipoAcuerdoRepository.findByMateriaIdAndTipoSistemaId(materia.getId(), tipoSistemaId);
            assertNotNull(tipoAcuerdos, "La lista no debe ser nula para tipoSistemaId " + tipoSistemaId);
            assertFalse(tipoAcuerdos.isEmpty(), "La lista no debe estar vacía para tipoSistemaId " + tipoSistemaId);
            assertTrue(tipoAcuerdos.stream().allMatch(t ->
                            t.getMateria().getId().equals(materia.getId()) &&
                                    t.getTipoSistema().getId().equals(tipoSistemaId)),
                    "Todos los TipoAcuerdo deben tener el materiaId y tipoSistemaId esperados para tipoSistemaId " + tipoSistemaId);
        });
    }
}