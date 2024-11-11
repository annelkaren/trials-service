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

import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
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
    public void saveTest(){
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
        given(documentoRepository.save(any(Documento.class))).willReturn(doc);
        given(documentoDetalleRepository.save(any(DocumentoDetalle.class))).willReturn(new DocumentoDetalle());
        given(documentoContenidoRepository.save(any(DocumentoContenido.class))).willReturn(new DocumentoContenido());
        given(movimientoService.createMovimento(null, doc, persona, "", EstadoCarpeta.CREADO.name())).willReturn(new Movimiento());


        DocumentoGenericRecord result =  acuerdosService.save(acuerdoRecord);
        
        assertNotNull(result);
    }

    
}
