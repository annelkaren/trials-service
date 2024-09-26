package mx.gob.pjpuebla.trials.workflow.folios;

import jakarta.persistence.EntityManager;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Disabled
class SecuenciaRepositoryCustomTest extends AuditConfigTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DocumentoRepository documentoRepository;

    @BeforeEach
    void setupSequences() {
        entityManager.createNativeQuery("CREATE SEQUENCE IF NOT EXISTS SEQ_DEMANDA_FOLIO START WITH 1").executeUpdate();
        entityManager.createNativeQuery("CREATE SEQUENCE IF NOT EXISTS SEQ_EXHORTO_FOLIO START WITH 1").executeUpdate();
        entityManager.createNativeQuery("CREATE SEQUENCE IF NOT EXISTS SEQ_PROMOCION_FOLIO START WITH 1").executeUpdate();
    }

    @Test
    void getIdByDemandaSecuence_success() {
        Long result = documentoRepository.getNextValFolio("SEQ_DEMANDA_FOLIO");
        assertThat(result)
                .isNotNull()
                .isPositive();
    }

    @Test
    void getIdByExhortoSecuence_success() {
        Long result = documentoRepository.getNextValFolio("SEQ_EXHORTO_FOLIO");
        assertThat(result)
                .isNotNull()
                .isPositive();
    }

    @Test
    void getIdByPromocionSecuence_success() {
        Long result = documentoRepository.getNextValFolio("SEQ_PROMOCION_FOLIO");
        assertThat(result)
                .isNotNull()
                .isPositive();
    }

}