package mx.gob.pjpuebla.trials.workflow.folios;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.oficialias.OficialiaRepository;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.TipoCentroTrabajo;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {"spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(value = {
        "/scripts/INSERT_DOMICILIOS.sql",
        "/scripts/INSERT_DISTRITOS.sql",
        "/scripts/INSERT_SEDES.sql",
        "/scripts/INSERT_MATERIAS.sql",
        "/scripts/INSERT_TIPO_SISTEMAS.sql",
        "/scripts/INSERT_TIPO_JUICIOS.sql",
        "/scripts/INSERT_TIPO_OFICIALIAS.sql",
        "/scripts/INSERT_OFICIALIAS.sql",
        "/scripts/INSERT_JUZGADOS.sql",
        "/scripts/INSERT_DOCUMENTO_FOLIO.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_DOCUMENTO_FOLIO.sql",
        "/scripts/DELETE_JUZGADO_FOLIOS.sql",
        "/scripts/DELETE_JUZGADOS.sql",
        "/scripts/DELETE_OFICIALIAS.sql",
        "/scripts/DELETE_TIPO_OFICIALIAS.sql",
        "/scripts/DELETE_TIPO_JUICIOS.sql",
        "/scripts/DELETE_TIPO_SISTEMAS.sql",
        "/scripts/DELETE_MATERIAS.sql",
        "/scripts/DELETE_SEDES.sql",
        "/scripts/DELETE_DISTRITOS.sql",
        "/scripts/DELETE_DOMICILIOS.sql",
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class DocumentoFoliosRepositoryTest extends AuditConfigTest {
    @Autowired
    private DocumentoFolioRepository documentoFolioRepository;

    @Autowired
    private JuzgadoRepository juzgadoRepository;

    @Autowired
    private OficialiaRepository oficialiaRepository;

    private Juzgado juzgado;
    private Oficialia oficialia;

    @Test
    void getFolioOficioJuzgado(){
        juzgado = juzgadoRepository.findAll().stream().findFirst().orElseThrow();
        Optional<DocumentoFolios> documentoFolios = documentoFolioRepository
                .findByCentroTrabajoAndTipoDocumento(juzgado.getId(), TipoCentroTrabajo.JUZGADO, TipoDocumento.OFICIO);

        assertThat(documentoFolios).isPresent();

    }

    @Test
    void getFolioOficioOficialia(){
        oficialia = oficialiaRepository.findAll().stream()
                .filter(of -> of.getTipoOficialia().getNombre().equals("Común")).findFirst().orElseThrow();
        Optional<DocumentoFolios> documentoFolios = documentoFolioRepository
                .findByCentroTrabajoAndTipoDocumento(oficialia.getId(), TipoCentroTrabajo.OFICIALIA_COMUN, TipoDocumento.OFICIO);

        assertThat(documentoFolios).isPresent();
    }

    @Test
    void updateFolioTest(){
        int i = 1;
        juzgado = juzgadoRepository.findAll().stream().findFirst().orElseThrow();
        DocumentoFolios documentoFolios = documentoFolioRepository
                .findByCentroTrabajoAndTipoDocumento(juzgado.getId(), TipoCentroTrabajo.JUZGADO, TipoDocumento.OFICIO).orElseThrow();

        for(; i<5; i++){
                documentoFolioRepository.updateFolio(documentoFolios.getId(), i);
        }

        DocumentoFolios documentoFolioNew = documentoFolioRepository
        .findByCentroTrabajoAndTipoDocumento(juzgado.getId(), TipoCentroTrabajo.JUZGADO, TipoDocumento.OFICIO).orElseThrow();

        assertThat(documentoFolioNew).hasFieldOrPropertyWithValue("folio", i);

    }
}