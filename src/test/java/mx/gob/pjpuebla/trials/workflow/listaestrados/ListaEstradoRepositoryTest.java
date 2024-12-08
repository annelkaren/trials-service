package mx.gob.pjpuebla.trials.workflow.listaestrados;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.workflow.listaestrados.ListaEstrado;
import mx.gob.pjpuebla.trials.workflow.listaestrados.ListaEstradoRepository;
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
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(value = {
        "/scripts/INSERT_LISTADO_ESTRADOS.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_LISTADO_ESTRADOS.sql",
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class ListaEstradoRepositoryTest extends AuditConfigTest {

        @Autowired
        private ListaEstradoRepository listaEstradoRepository;

        @Test
        void findAllListaEstradoIdAndSearch(){
            Page<ListaEstrado> entity = listaEstradoRepository.findAllListaEstradoIdAndSearch("",null, PageRequest.of(0, 20));
            assertThat(entity).isNotEmpty();
            assertThat(entity.getContent().get(0).getUsuarioAlta()).isEqualTo("anibaldemar");
            assertThat(entity.getContent().get(1).getUsuarioAlta()).isEqualTo("anibaldemar");
        }
}