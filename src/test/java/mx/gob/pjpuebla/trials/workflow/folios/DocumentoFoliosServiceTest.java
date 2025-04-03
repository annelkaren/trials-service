package mx.gob.pjpuebla.trials.workflow.folios;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.oficialias.OficialiaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialia;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaSetUp;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.TipoCentroTrabajo;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento; 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DocumentoFoliosServiceTest {
    @Mock
    private DocumentoFolioRepository documentoFolioRepository;

    @InjectMocks
    private DocumentoFoliosService documentoFoliosService;

    @Mock
    private JuzgadoRepository juzgadoRepository;

    private Juzgado juzgado;
    private Oficialia oficialia;
    private DocumentoFolios documentoFoliosJuzgado;
    private DocumentoFolios documentoFoliosOficialia;

    @BeforeEach
    void setUp(){
        Materia materia = MateriaSetUp.createMateria();

        Distrito distrito = DistritoSetUp.createDistrito();
        Domicilio domicilio = DomicilioSetUp.createDomicilio();

        TipoOficialia tipoOficialia = TipoOficialiaSetUp.createtipoOficialia();
        tipoOficialia.setNombre("Común");
        Sede sede = SedeSetUp.createSede();
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);

        juzgado = JuzgadoSetUp.createJuzgado(materia, sede);
        juzgado.setContadorAsignaciones(0);
        juzgado.setMaxAsignacionesRonda(0);

        oficialia = OficialiaSetUp.createOficialia(tipoOficialia, sede);

        documentoFoliosJuzgado = new DocumentoFolios()
                .setFolio(0)
                .setTipoDocumento(TipoDocumento.OFICIO)
                .setCentroTrabajoId(juzgado.getId())
                .setTipoCentroTrabajo(TipoCentroTrabajo.JUZGADO)
                .setYear(LocalDate.now().getYear());

        documentoFoliosOficialia = new DocumentoFolios()
                .setFolio(0)
                .setTipoDocumento(TipoDocumento.OFICIO)
                .setCentroTrabajoId(oficialia.getId())
                .setTipoCentroTrabajo(TipoCentroTrabajo.OFICIALIA_COMUN)
                .setYear(LocalDate.now().getYear());

    }

    @Test
    void getFolioJuzgadoTest(){
        given(documentoFolioRepository.findByCentroTrabajoAndTipoDocumento(any(), any(), any())).willReturn(Optional.of(documentoFoliosJuzgado));
        given(documentoFoliosService.create(any())).willReturn(documentoFoliosJuzgado);
        Integer folio = documentoFoliosService.getFolio(TipoDocumento.OFICIO, juzgado, null);

        assertThat(folio).isEqualTo(1);

        for (int i=1;i<5;i++){
            documentoFoliosJuzgado.setFolio(i-1);
            given(documentoFolioRepository.findByCentroTrabajoAndTipoDocumento(any(), any(), any())).willReturn(Optional.of(documentoFoliosJuzgado));
            assertThat(documentoFoliosService.getFolio(TipoDocumento.OFICIO, juzgado, null)).isEqualTo(i);
        }
    }

    @Test
    void getFolioOficialiaTest(){
        documentoFoliosOficialia.setFolio(1);
        given(documentoFolioRepository.findByCentroTrabajoAndTipoDocumento(any(), any(), any())).willReturn(Optional.of(documentoFoliosOficialia));
        given(documentoFoliosService.create(any())).willReturn(documentoFoliosOficialia);
        Integer folio = documentoFoliosService.getFolio(TipoDocumento.OFICIO, null, oficialia);

        assertThat(folio).isEqualTo(2);
    }

    @Test
    void getFolioOficialiaTestFail(){
        TipoOficialia tipoOficialia = TipoOficialiaSetUp.createtipoOficialia();
        tipoOficialia.setNombre("Mayor");

        oficialia.setId(2);
        oficialia.setTipoOficialia(tipoOficialia);
        documentoFoliosOficialia.setCentroTrabajoId(oficialia.getId());

        NotFoundException assertThrows  = assertThrows(NotFoundException.class, 
        () -> documentoFoliosService.getFolio(TipoDocumento.OFICIO, null, oficialia));

        assertThat(assertThrows.getMessage()).contains("Centro de Trabajo NO compatible");
    }

    @Test
    void saveDocumentoFolioTest(){
        juzgado.setId(3);
        documentoFoliosJuzgado.setCentroTrabajoId(3);

        given(juzgadoRepository.existsById(any())).willReturn(Boolean.TRUE);
        given(documentoFolioRepository.save(any())).willReturn(documentoFoliosJuzgado);

        documentoFoliosJuzgado = documentoFoliosService.save(documentoFoliosJuzgado);

        assertThat(documentoFoliosJuzgado).isNotNull().hasFieldOrPropertyWithValue("centroTrabajoId", juzgado.getId());

    }
}
