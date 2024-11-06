package mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;

import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records.AcuerdoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenido;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;

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

    @Test
    public void saveTest(){
        AcuerdoRecord acuerdoRecord = AcuerdoRecordSetUp.create();
        
        Carpeta carpeta = CarpetaSetUp.create();
        
        DocumentoData docData = new DocumentoData();
        docData.setRubros(acuerdoRecord.rubros());
        
        Documento doc = new Documento();

        doc.setCarpeta(carpeta);
        doc.setTipoDocumento(TipoDocumento.ACUERDO);
        doc.setData(docData);

        given(carpetaRepository.findById(acuerdoRecord.carpetaId())).willReturn(Optional.of(carpeta));
        given(documentoRepository.save(doc)).willReturn(doc);
        given(documentoDetalleRepository.save(any(DocumentoDetalle.class))).willReturn(new DocumentoDetalle());
        given(documentoContenidoRepository.save(any(DocumentoContenido.class))).willReturn(new DocumentoContenido());

        Documento result =  acuerdosService.save(acuerdoRecord);
        
        assertNotNull(result);
    }

    
}
