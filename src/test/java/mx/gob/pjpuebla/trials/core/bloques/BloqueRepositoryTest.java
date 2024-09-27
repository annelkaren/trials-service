package mx.gob.pjpuebla.trials.core.bloques;

import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@DataJpaTest(properties = {
    "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Disabled
class BloqueRepositoryTest extends AuditConfigTest  {

    @Autowired
    private BloqueRepository bloqueRepository;

    @Test   
    void findByHoraInicialSuccess() {
        LocalTime horaInicial = LocalTime.of(8, 30, 0);
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<Bloque> entity = bloqueRepository.findByHoraInicial(horaInicial, pageable);
        assertThat(entity).isEmpty();  
    }

    @Test   
    void findByHoraInicialFail() {
        LocalTime horaInicial = LocalTime.of(8, 30, 0);
        Pageable pageable = PageRequest.of(0, 10);
        Bloque b = BloqueSetUp.createBloque();
        b.setHoraInicial(horaInicial);
        
        bloqueRepository.save(b);

        Page<Bloque> entity = bloqueRepository.findByHoraInicial(horaInicial, pageable);
        assertThat(entity).isNotEmpty();  
    }

    @Test
    void findByIdAndEstadoActive() {
        Bloque bloque = BloqueSetUp.createBloque(Estado.ACTIVE);
        bloque = bloqueRepository.save(bloque);
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Optional<BloqueRecordResponse> entity = bloqueRepository.findByIdAndEstadoIn(bloque.getId(), estados);
        assertThat(entity).isPresent();
        assertThat(entity.get().estado()).isEqualTo(Estado.ACTIVE);
    }
}
