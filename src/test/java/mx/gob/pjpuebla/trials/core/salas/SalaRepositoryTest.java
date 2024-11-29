package mx.gob.pjpuebla.trials.core.salas;

import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.bloques.BloqueCitaItem;
import mx.gob.pjpuebla.trials.core.bloques.BloqueData;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRepository;
import mx.gob.pjpuebla.trials.core.bloques.BloqueSetUp;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop",
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(value = {
        "/scripts/INSERT_DOMICILIOS.sql",
        "/scripts/INSERT_ESCOLARIDADES.sql",
        "/scripts/INSERT_ESTADO_CIVIL.sql",
        "/scripts/INSERT_DISTRITOS.sql",
        "/scripts/INSERT_SEDES.sql",
        "/scripts/INSERT_MATERIAS.sql",
        "/scripts/INSERT_TIPO_SISTEMAS.sql",
        "/scripts/INSERT_TIPO_JUICIOS.sql",
        "/scripts/INSERT_JUZGADOS.sql",
        "/scripts/INSERT_JUZGADO_TIPOJUICIO.sql",
        "/scripts/INSERT_PERSONAS.sql",
        "/scripts/INSERT_BLOQUES.sql",
        "/scripts/INSERT_SALAS.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_SALAS.sql",
        "/scripts/DELETE_BLOQUES.sql",
        "/scripts/DELETE_PERSONAS.sql",
        "/scripts/DELETE_JUZGADO_TIPOJUICIO.sql",
        "/scripts/DELETE_JUZGADOS.sql",
        "/scripts/DELETE_TIPO_JUICIOS.sql",
        "/scripts/DELETE_TIPO_SISTEMAS.sql",
        "/scripts/DELETE_MATERIAS.sql",
        "/scripts/DELETE_SEDES.sql",
        "/scripts/DELETE_DISTRITOS.sql",
        "/scripts/DELETE_ESTADO_CIVIL.sql",
        "/scripts/DELETE_ESCOLARIDADES.sql",
        "/scripts/DELETE_DOMICILIOS.sql",
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class SalaRepositoryTest extends AuditConfigTest {

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private BloqueRepository bloqueRepository;

    @Autowired
    private JuzgadoRepository juzgadoRepository;

    @Test
    void findByIdAndEstado() {

        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Optional<SalaRecordResponse> entity = salaRepository.findByIdAndEstadoIn(1, estados);

        assertThat(entity).isPresent();
        assertThat(entity.get().estado()).isEqualTo(Estado.ACTIVE);
    }

    @Test
    void testCountByJuzgadoId() {
        long count = salaRepository.countByJuzgadoId(51);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testCountByJuzgadoId_noSalas() {
        long count = salaRepository.countByJuzgadoId(53);

        assertThat(count).isZero();
    }

    @Test
    void testFindSalaDisponible(){
        BloqueCitaItem cita = new BloqueCitaItem();
        Juzgado juzgado = juzgadoRepository.findAll().stream().findFirst().orElse(null);
        Sala sala = salaRepository.findByJuzgado(juzgado).stream().findFirst().orElse(null);
        Bloque bloque = sala.getBloque();

        cita.setNumCitas(1);
        cita.setHoraCitas(LocalTime.of(8,30,00));

        bloque.setData(new BloqueData().setCitas(Arrays.asList(cita)));
        bloque = bloqueRepository.save(BloqueSetUp.createBloque());
        sala.setBloque(bloque);

        salaRepository.save(sala);

        List<Sala> salas = salaRepository.findSalaDisponible(LocalDateTime.now(), bloque, juzgado);

        assertThat(salas).isNotEmpty();
    }

    @Test
    void testHorarioDisponible(){
        BloqueCitaItem cita = new BloqueCitaItem();
        LocalDateTime fechaAudiciencia = LocalDateTime.of(LocalDate.now().plusDays(3), LocalTime.of(8,30,00));
        Juzgado juzgado = juzgadoRepository.findAll().stream().findFirst().orElse(null);
        Sala sala = salaRepository.findByJuzgado(juzgado).stream().findFirst().orElse(null);
        Bloque bloque = sala.getBloque();

        cita.setNumCitas(1);
        cita.setHoraCitas(LocalTime.of(8,30,00));

        bloque.setData(new BloqueData().setCitas(Arrays.asList(cita)));
        bloque = bloqueRepository.save(BloqueSetUp.createBloque());

        sala.setBloque(bloque);

        sala = salaRepository.save(sala);


        Optional<Sala> salas = salaRepository.checkHoraDisponible(fechaAudiciencia, sala);

        assertThat(salas).isPresent();
    }

    @Test
    void testFindByJuzgadoAndNombreContainingIgnoreCase() {
        Juzgado juzgado = juzgadoRepository.findAll().stream().findFirst().orElse(null);
        assertThat(juzgado).isNotNull();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Sala> result = salaRepository.findByJuzgadoAndNombreContainingIgnoreCase(juzgado, "", pageable);
        assertThat(result).isNotNull();
    }


}
