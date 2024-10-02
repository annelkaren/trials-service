package mx.gob.pjpuebla.trials.core.bloques;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(value = {
        "/scripts/INSERT_BLOQUES.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_BLOQUES.sql",
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@Disabled
class BloqueRepositoryTest extends AuditConfigTest {

    @Autowired
    private BloqueRepository bloqueRepository;

    @Test
    void findByHoraInicialSuccess() {
        LocalTime horaInicial = LocalTime.of(8, 30, 0);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Bloque> entity = bloqueRepository.findByHoraInicial(horaInicial, pageable);
        assertThat(entity.getContent()).isEmpty();
    }

    @Test
    void findByHoraInicialFail() {
        LocalTime horaInicial = LocalTime.of(8, 30, 0);
        Pageable pageable = PageRequest.of(0, 10);
        Bloque b = BloqueSetUp.createBloque();
        b.setHoraInicial(horaInicial);

        bloqueRepository.save(b);

        Page<Bloque> entity = bloqueRepository.findByHoraInicial(horaInicial, pageable);
        assertThat(entity.getContent()).isNotEmpty();
    }

    @Test
    void findByIdAndEstadoActive() {
        Bloque bloque = BloqueSetUp.createBloque();
        bloque = bloqueRepository.save(bloque);
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Optional<BloqueRecordResponse> entity = bloqueRepository.findByIdAndEstadoIn(bloque.getId(), estados);
        assertThat(entity).isPresent();
        assertThat(entity.get().estado()).isEqualTo(Estado.ACTIVE);
    }
}
