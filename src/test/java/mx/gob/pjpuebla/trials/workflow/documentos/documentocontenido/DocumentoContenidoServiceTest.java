package mx.gob.pjpuebla.trials.workflow.documentos.documentocontenido;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.gob.pjpuebla.trials.core.instituciones.InstitucionRepository;
import mx.gob.pjpuebla.trials.core.instituciones.InstitucionSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenido;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoService;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoOficioDigitalizacionRecord;

@ExtendWith(MockitoExtension.class)
class DocumentoContenidoServiceTest {

    @Mock
    private DocumentoRepository documentoRepository;

    @Mock
    private DocumentoContenidoRepository documentoContenidoRepository;

    @Mock
    private InstitucionRepository institucionRepository;

    @Mock
    private DocumentoDetalleRepository documentoDetalleRepository;

    @InjectMocks
    private DocumentoContenidoService documentoContenidoService;

    @Test
    void getDataDocumentoDigitalizacion() {

        given(documentoRepository.findById(anyInt()))
                .willReturn(Optional.of(DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio())
                        .setTipoDocumento(TipoDocumento.PROMOCION)));

        given(documentoContenidoRepository.findByDocumentoId(anyInt()))
            .willReturn(Optional.of(new DocumentoContenido()));

        DocumentoOficioDigitalizacionRecord doc = documentoContenidoService.getDataDocumentoDigitalizacion(1);

        assertNotNull(doc, "El DocumentoOficioDigitalizacionRecord no debe ser nulo");

    }

    @Test
    void cancelarOficio() {

        given(documentoRepository.findById(anyInt()))
                .willReturn(Optional.of(DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio())
                        .setTipoDocumento(TipoDocumento.PROMOCION)));

        Integer result = documentoContenidoService.cancelarOficio(anyInt());

        assertEquals(1, result);
    }

    @Test
    void updateDocumentoOficioDigitalizacion() {
        given(documentoRepository.findById(anyInt()))
                .willReturn(Optional.of(DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio())
                        .setTipoDocumento(TipoDocumento.PROMOCION)));

        given(institucionRepository.findById(anyInt()))
                .willReturn(Optional.of(InstitucionSetUp.createInstitucion(Estado.ACTIVE)));

        given(documentoContenidoRepository.findByDocumentoId(anyInt()))
            .willReturn(Optional.of(new DocumentoContenido()));

        given(documentoDetalleRepository.findByDocumentoId(anyInt()))
            .willReturn(Optional.of(new DocumentoDetalle()));

        DocumentoOficioDigitalizacionRecord oficio = documentoContenidoService
                .updateDocumentoOficioDigitalizacion(DocumentoContenidoSetUp.documentoOficioDigitalizacionRecordSetUp());

        assertNotNull(oficio, "El record no deberia ser nulo");
        assertEquals("2", oficio.numeroFolio());
        assertEquals("00000/2024", oficio.expediente());
        assertEquals(LocalDate.now(), oficio.fechaEmision());
        assertEquals(1, oficio.idOficio());
        assertEquals(LocalDate.now(), oficio.fechaEntrega());
        assertEquals(EstadoCarpeta.ASIGNADO, oficio.estatus());
        assertEquals("asunto prueba", oficio.asunto());

    }

}
