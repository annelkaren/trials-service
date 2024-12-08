package mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;

import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoNotificadosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoPromocionesRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenido;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoGenericRecord;
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;

@ExtendWith(MockitoExtension.class)
class AcuerdoServiceTest {

    @InjectMocks
    private AcuerdosService acuerdosService;
    
    @Mock
    private DocumentoRepository documentoRepository;

    @Mock
    private DocumentoDetalleRepository documentoDetalleRepository;

    @Mock
    private DocumentoContenidoRepository documentoContenidoRepository;

    @Mock
    private CarpetaRepository carpetaRepository;

    @Mock
    private MovimientoService movimientoService;

    @Mock
    private PersonaService personaService;

    @Test
    void saveTest() {
        AcuerdoRecord acuerdoRecord = AcuerdoRecordSetUp.create();
        Persona persona = PersonaSetUp.createPersona();
        Carpeta carpeta = CarpetaSetUp.create();
        
        DocumentoData docData = new DocumentoData();
        docData.setRubros(acuerdoRecord.rubros());
        
        Documento doc = new Documento();
        doc.setCarpeta(carpeta);
        doc.setTipoDocumento(TipoDocumento.ACUERDO);
        doc.setData(docData);

        given(carpetaRepository.findById(acuerdoRecord.carpetaId())).willReturn(Optional.of(carpeta));
        given(documentoRepository.findById(anyInt())).willReturn(Optional.of(new Documento()));
        given(documentoRepository.save(any(Documento.class))).willReturn(doc);
        given(documentoDetalleRepository.save(any(DocumentoDetalle.class))).willReturn(new DocumentoDetalle());
        given(documentoContenidoRepository.save(any(DocumentoContenido.class))).willReturn(new DocumentoContenido());
        lenient().when(movimientoService.createMovimento(null, doc, persona, "", EstadoCarpeta.CREADO.name())).thenReturn(new Movimiento());

        DocumentoGenericRecord result = acuerdosService.save(acuerdoRecord);
        
        assertNotNull(result);
    }

    @Test
    void obtenerPromocionesTest(){
        Integer carpetaId = 1;
        String actualizacion = "NO";

        List<AcuerdoPromocionesRecord> result = acuerdosService.obtenerPromociones(carpetaId, actualizacion);
        assertNotNull(result);
    }

    @Test
    void getAcuerdos() {
        Integer carpetaId = 1;
        List<AcuerdosRecord> acuerdo = AcuerdoRecordSetUp.createAcuerdoRecord();
    
        given(documentoRepository.findAllAcuerdosByCarpeta(anyInt(), any(PageRequest.class)))
            .willReturn(new PageImpl<>(acuerdo, PageRequest.of(0, acuerdo.size()), acuerdo.size()));
    
        Page<AcuerdosRecord> page = acuerdosService.getAcuerdos(carpetaId, PageRequest.of(0, acuerdo.size()));
    
        assertThat(page.getContent())
            .hasSize(acuerdo.size())
            .first();
    }

    @Test
    void publicarAcuerdo() {
        AcuerdoRecord acuerdo = AcuerdoRecordSetUp.create();
        
        Documento doc = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());

        given(documentoRepository.findById(anyInt())).willReturn(Optional.of(doc));
        given(documentoContenidoRepository.findByDocumentoId(anyInt())).willReturn(Optional.of(new DocumentoContenido()));
        given(documentoDetalleRepository.findByDocumentoId(anyInt())).willReturn(Optional.of(new DocumentoDetalle()));

        AcuerdoRecord response = acuerdosService.publicarAcuerdo(acuerdo);

        assertNotNull(response);
    }

    @Test
    void getTipoPartesAcuerdo(){
        List<AcuerdoNotificadosRecord> acuerdoNotificado = acuerdosService.getTipoPartesAcuerdo(anyInt(), anyString());

        assertNotNull(acuerdoNotificado);
    
    }

    @Test
    void getAcuerdoTest(){
        AcuerdoRecord acuerdoRecord = AcuerdoRecordSetUp.create();
        DocumentoData docData = new DocumentoData();
        docData.setRubros(acuerdoRecord.rubros());

        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        documento.setData(docData);

        documento.setData(new DocumentoData());
        given(documentoRepository.findById(anyInt()))
            .willReturn(Optional.of(documento));

        given(documentoDetalleRepository.findByDocumentoId(anyInt()))
            .willReturn(Optional.of(new DocumentoDetalle()));

        given(documentoContenidoRepository.findByDocumentoId(anyInt()))
            .willReturn(Optional.of(new DocumentoContenido()));

        AcuerdoRecord result = acuerdosService.getAcuerdo(1);

        assertNotNull(result);
    }

    @Test
    void update(){
        AcuerdoRecord acuerdoRecord = AcuerdoRecordSetUp.create();
        Carpeta carpeta = CarpetaSetUp.create();
        
        DocumentoData docData = new DocumentoData();
        docData.setRubros(acuerdoRecord.rubros());
        
        Documento doc = new Documento();
        doc.setCarpeta(carpeta);
        doc.setTipoDocumento(TipoDocumento.ACUERDO);
        doc.setData(docData);

        given(documentoRepository.findById(anyInt())).willReturn(Optional.of(doc));
        given(documentoRepository.save(any(Documento.class))).willReturn(doc);
        
        given(documentoDetalleRepository.findByDocumentoId(anyInt())).willReturn(Optional.of(new DocumentoDetalle()));
        given(documentoDetalleRepository.save(any(DocumentoDetalle.class))).willReturn(new DocumentoDetalle());
        
        given(documentoContenidoRepository.findByDocumentoId(anyInt())).willReturn(Optional.of(new DocumentoContenido()));
        given(documentoContenidoRepository.save(any(DocumentoContenido.class))).willReturn(new DocumentoContenido());
        
        DocumentoGenericRecord result = acuerdosService.update(acuerdoRecord);
        
        assertNotNull(result);
    }

    @Test
    void testfindPromocionesAcuerdo(){
        Documento promocion = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        List<Documento> promociones = Collections.singletonList(promocion);

        given(documentoRepository.findByAcuerdoRespuestaId(any())).willReturn(promociones);

        List<AcuerdoPromocionesRecord> result = acuerdosService.findPromocionesByAcuerdo(1);

        assertThat(result).isNotEmpty();
    }

}
