package mx.gob.pjpuebla.trials.workflow.listaestrados;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.workflow.notificaciones.Notificacion;
import mx.gob.pjpuebla.trials.workflow.notificaciones.NotificacionRepository;
import net.sf.jasperreports.engine.JRException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ListaEstradoServiceTest {

    @Mock
    ListaEstradoRepository listaEstradoRepository;
    @Mock
    NotificacionRepository notificacionRepository;
    @Mock
    ListaEstradoGenerator generator;
    @Mock
    DocumentoDetalleRepository documentoDetalleRepository;
    @Mock
    TipoSistemaRepository tipoSistemaRepository;
    @Mock
    PersonaService personaService;

    @InjectMocks
    ListaEstradoService target;

    private ListaEstrado listaEstrado;

    @BeforeEach
    public void setUp() {
        listaEstrado = ListaEstradoSetUp.createLisEstrado();
    }

    @Test
    void testFindAllByListaEstradoIdWithNullSearchQuery() {
        when(listaEstradoRepository.findAllListaEstradoIdAndSearch(any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(listaEstrado)));

        when(notificacionRepository.countNotificacionesByListaEstradoId(any())).thenReturn(5L);

        Page<ListaEstradoRecord> result = target.findAllByListaEstradoId(1, null, PageRequest.of(0, 20));

        assertNotNull(result);

        verify(listaEstradoRepository).findAllListaEstradoIdAndSearch("", 1, PageRequest.of(0, 20));

        verify(notificacionRepository).countNotificacionesByListaEstradoId(listaEstrado.getId());
    }

    @Test
    void testFindAllByListaEstradoIdWithEmptySearchQuery() {
        when(listaEstradoRepository.findAllListaEstradoIdAndSearch(any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(listaEstrado)));

        when(notificacionRepository.countNotificacionesByListaEstradoId(any())).thenReturn(5L);

        Page<ListaEstradoRecord> result = target.findAllByListaEstradoId(1, "", PageRequest.of(0, 20));

        assertNotNull(result);

        verify(listaEstradoRepository).findAllListaEstradoIdAndSearch("", 1, PageRequest.of(0, 20));

        verify(notificacionRepository).countNotificacionesByListaEstradoId(listaEstrado.getId());
    }

    @Test
    void getReporteListaEstrados() throws IOException, JRException {
        Integer id = 1;
        Materia materia = MateriaSetUp.createMateria();
        TipoSistema tipoSistema = tipoSistemaRepository.save(TipoSistemaSetUp.createTipoSistema());
        TipoJuicio tipoJuicio = TipoJuicioSetUp.createTipoJuicio(tipoSistema, materia);
        
        Carpeta carpeta = new Carpeta()
                .setId(1)
                .setVersion(1)
                .setFolio("1")
                .setExpediente("000001/2024")
                .setEstatus(EstadoCarpeta.CAPTURA)
                .setTipoJuicio(tipoJuicio)
                .setTipoCarpeta(TipoCarpeta.DEMANDA)
                .setSelloEstatus(SelloEstatus.VALIDO)
                .setJuzgado(JuzgadoSetUp.createJuzgado());
        
        DocumentoData docdata = new DocumentoData()
            .setRubros(List.of("RUBRO1", "RUBRO2")); 
        Documento documento = new Documento()
            .setId(1)
            .setCarpeta(carpeta)
            .setTipoDocumento(TipoDocumento.ACUERDO)
            .setData(docdata);
        List<Notificacion> notificacionList = List.of(new Notificacion().setDocumento(documento));
        Persona persona = new Persona();
        persona.setJuzgado(new Juzgado().setNombre("Juzgado Prueba"));

        DocumentoDetalle documentoDetalle = new DocumentoDetalle();
        documentoDetalle.setFechaResolucion(LocalDate.now());

        byte[] pdfMock = new byte[]{1, 2, 3};

        given(notificacionRepository.getNotificacionByTipo(id)).willReturn(notificacionList);
        given(personaService.getAuditor()).willReturn(persona);
        given(documentoDetalleRepository.findByDocumentoId(anyInt())).willReturn(Optional.of(documentoDetalle));
        given(generator.getReporteListaEstrados(any())).willReturn(pdfMock);

        ResponseEntity<Object> response = target.getReporteListaEstrados(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(byte[].class).isEqualTo(pdfMock);
    }
}