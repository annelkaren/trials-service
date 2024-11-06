package mx.gob.pjpuebla.trials.core.etapaprocesal;

import mx.gob.pjpuebla.trials.core.etapaprocesal.record.EtapaProcesalRecord;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(value = {
        "/scripts/INSERT_TIPO_SISTEMAS.sql",
        "/scripts/INSERT_MATERIAS.sql",
        "/scripts/INSERT_ETAPA_PROCESAL.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_ETAPA_PROCESAL.sql",
        "/scripts/DELETE_TIPO_SISTEMAS.sql",
        "/scripts/DELETE_MATERIAS.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class EtapaProcesalRepositoryTest extends AuditConfigTest {

    @Autowired
    private EtapaProcesalRepository etapaProcesalRepository;

    @Test
    void getListEtapaProcesal(){
        List<EtapaProcesalRecord> list = etapaProcesalRepository.getListEtapaProcesalByTipoJuicioAndProcedimiento(
                150,  null
        );
        assertThat(list).isNotEmpty()
                .isNotNull();
    }

    @Test
    void getListEtapaProcesal_isEmpty(){
        List<EtapaProcesalRecord> list = etapaProcesalRepository.getListEtapaProcesalByTipoJuicioAndProcedimiento(
                1150,  null
        );
        assertThat(list).isEmpty();
    }

}