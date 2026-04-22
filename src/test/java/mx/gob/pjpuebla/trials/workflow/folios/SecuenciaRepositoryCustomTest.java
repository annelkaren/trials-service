package mx.gob.pjpuebla.trials.workflow.folios;

import jakarta.persistence.EntityManager;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class SecuenciaRepositoryCustomTest extends AuditConfigTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SecuenciaService secuenciaService;

    @BeforeEach
    void setupSequences() {
        entityManager.createNativeQuery("CREATE SEQUENCE IF NOT EXISTS SEQ_DEMANDA_FOLIO START WITH 1").executeUpdate();
        entityManager.createNativeQuery("CREATE SEQUENCE IF NOT EXISTS SEQ_EXHORTO_FOLIO START WITH 1").executeUpdate();
        entityManager.createNativeQuery("CREATE SEQUENCE IF NOT EXISTS SEQ_PROMOCION_FOLIO START WITH 1")
                .executeUpdate();
    }

    @Test
    void getIdByDemandaSecuence_success() {
        Long result = secuenciaService.getNextValDemanda();
        assertThat(result)
                .isNotNull()
                .isPositive();
    }

    @Test
    void getIdByExhortoSecuence_success() {
        Long result = secuenciaService.getNextValExhorto();
        assertThat(result)
                .isNotNull()
                .isPositive();
    }

    @Test
    void getIdByPromocionSecuence_success() {
        Long result = secuenciaService.getNextValPromocion();
        assertThat(result)
                .isNotNull()
                .isPositive();
    }

}