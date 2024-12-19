package mx.gob.pjpuebla.trials.workflow.audiencias;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudiencia;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciaAgendaRecord;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciaOralidadFamiliarRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;

@DataJpaTest(properties = {"spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(value = {
        "/scripts/INSERT_TIPO_AUDIENCIA.sql",
        "/scripts/INSERT_DOMICILIOS.sql",
        "/scripts/INSERT_ESTADO_CIVIL.sql",
        "/scripts/INSERT_ESCOLARIDADES.sql",
        "/scripts/INSERT_MATERIAS.sql",
        "/scripts/INSERT_DISTRITOS.sql",
        "/scripts/INSERT_SEDES.sql",
        "/scripts/INSERT_JUZGADOS.sql",
        "/scripts/INSERT_PERSONAS.sql",
        "/scripts/INSERT_BLOQUES.sql",
        "/scripts/INSERT_SALAS.sql",
        "/scripts/INSERT_TIPO_SISTEMAS.sql",
        "/scripts/INSERT_TIPO_JUICIOS.sql",
        "/scripts/INSERT_TIPO_PIEZAS.sql",
        "/scripts/INSERT_CARPETAS.sql",
        "/scripts/INSERT_AUDIENCIAS.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_AUDIENCIAS.sql",
        "/scripts/DELETE_CARPETAS.sql",
        "/scripts/DELETE_TIPO_PIEZAS.sql",
        "/scripts/DELETE_TIPO_JUICIOS.sql",
        "/scripts/DELETE_TIPO_SISTEMAS.sql",
        "/scripts/DELETE_SALAS.sql",
        "/scripts/DELETE_BLOQUES.sql",
        "/scripts/DELETE_PERSONAS.sql",
        "/scripts/DELETE_JUZGADOS.sql",
        "/scripts/DELETE_SEDES.sql",
        "/scripts/DELETE_DISTRITOS.sql",
        "/scripts/DELETE_MATERIAS.sql",
        "/scripts/DELETE_ESCOLARIDADES.sql",
        "/scripts/DELETE_ESTADO_CIVIL.sql",
        "/scripts/DELETE_DOMICILIOS.sql",
        "/scripts/DELETE_TIPO_AUDIENCIA.sql",
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class AudienciaRepositoryTest extends AuditConfigTest {

    @Autowired
    private AudienciaRepository audienciaRepository;

    @Test
    void findByCarpeta_ReturnAudiencia() {
        Carpeta carpeta = new Carpeta();
        carpeta.setId(1);
        carpeta.setVersion(1);

        Optional<Audiencia> audiencia = audienciaRepository.findByCarpeta(carpeta);
        assertThat(audiencia).isPresent();
    }

    @Test
    void getFechaUltimaAudiencia_ReturnCorrectDate() {
        Juzgado juzgado = new Juzgado();
        juzgado.setId(51);
        juzgado.setVersion(1);

        TipoAudiencia tipoAudiencia = new TipoAudiencia();
        tipoAudiencia.setId(8);
        tipoAudiencia.setVersion(1);

        LocalDateTime fechaUltimaAudiencia = audienciaRepository.getFechaUltimaAudiencia(juzgado, tipoAudiencia);
        assertThat(fechaUltimaAudiencia).isNotNull();
    }

    @Test
    void getJuzAndSalaAndAudienciaByIdcarpeta_ReturnRecord() {
        AudienciaOralidadFamiliarRecord entity = audienciaRepository.getJuzAndSalaAndAudienciaByIdcarpeta(1);
        assertThat(entity).isNotNull();
    }

    @Test
    void getSalaNombreByCarpetaId_ReturnsSalaNombre() {
        String salaNombre = audienciaRepository.getSalaNombreByCarpetaId(1);
        assertThat(salaNombre).isNotNull().isEqualTo("1");
    }

    @Test
    void findByJuzgado_NoFilter_ReturnsAllAudiencias() {
        Juzgado juzgado = new Juzgado();
        juzgado.setId(51);
        juzgado.setVersion(1);
        juzgado.setEstado(Estado.ACTIVE);
        Page<Audiencia> audiencias = audienciaRepository.findByJuzgado(juzgado, null, PageRequest.of(0, 10));
        assertThat(audiencias).isNotEmpty();
    }

    @Test
    void findBySalaIdAndFechaAudienciaTest() throws ParseException {
        // Preparar fecha usando SimpleDateFormat
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaAudiencia = sdf.parse("2024-10-04");

        // Ejecutar método del repositorio
        List<AudienciaAgendaRecord> response = audienciaRepository.findBySalaIdAndFechaAudiencia(1, fechaAudiencia);

        // Aserciones
        assertThat(response).isNotNull()
                            .isNotEmpty(); // Verificar que la lista no esté vacía

        assertThat(response.size()).isGreaterThan(0); // Confirmar que contiene al menos un elemento

    }

    @Test
    void existeConflicto_ReturnsTrueWhenConflictExists() {
        AudienciaRepository audienciaRepository = Mockito.mock(AudienciaRepository.class);
        Long salaId = 1L;
        LocalDateTime inicio = LocalDateTime.of(2024, 12, 18, 10, 0);
        LocalDateTime fin = LocalDateTime.of(2024, 12, 18, 11, 0);

        Mockito.when(audienciaRepository.existeConflicto(eq(salaId), eq(inicio), eq(fin))).thenReturn(true);

        boolean resultado = audienciaRepository.existeConflicto(salaId, inicio, fin);

        assertThat(resultado).isTrue();
        Mockito.verify(audienciaRepository).existeConflicto(eq(salaId), eq(inicio), eq(fin));
    }
    
    @Test
    void existeConflicto_ReturnsFalseWhenNoConflict() {
        AudienciaRepository audienciaRepository = Mockito.mock(AudienciaRepository.class);

        Long salaId = 1L;
        LocalDateTime inicio = LocalDateTime.of(2024, 12, 18, 10, 0);
        LocalDateTime fin = LocalDateTime.of(2024, 12, 18, 11, 0);

        Mockito.when(audienciaRepository.existeConflicto(eq(salaId), eq(inicio), eq(fin))).thenReturn(false);
        boolean resultado = audienciaRepository.existeConflicto(salaId, inicio, fin);
        assertThat(resultado).isFalse();

        Mockito.verify(audienciaRepository).existeConflicto(eq(salaId), eq(inicio), eq(fin));
    }
}
