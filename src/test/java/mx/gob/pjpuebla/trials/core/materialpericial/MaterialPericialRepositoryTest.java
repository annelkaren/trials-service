package mx.gob.pjpuebla.trials.core.materialpericial;


import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest(properties = {"spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(value = {
        "/scripts/INSERT_MATERIAL_PERICIAL.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_MATERIAL_PERICIAL.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class MaterialPericialRepositoryTest extends AuditConfigTest {

    @Autowired
    private MaterialPericialRepository materialPericialRepository;

    @Test
    void testGetAllMateriaPericial() {
        String nombre = "documento";
        List<MaterialPericial> results = materialPericialRepository.getAllMateriaPericial(nombre);

        assertThat(results).isNotEmpty();
        assertThat(results).hasSizeGreaterThan(0);
        assertThat(results.get(0).getNombre()).containsIgnoringCase(nombre);
    }


}