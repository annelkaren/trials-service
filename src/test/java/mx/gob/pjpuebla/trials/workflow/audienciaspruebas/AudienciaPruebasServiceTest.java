package mx.gob.pjpuebla.trials.workflow.audienciaspruebas;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import mx.gob.pjpuebla.trials.core.materiapericial.MateriaPericial;
import mx.gob.pjpuebla.trials.core.materiapericial.MateriaPericialRepository;
import mx.gob.pjpuebla.trials.core.tipoprueba.TipoPruebas;
import mx.gob.pjpuebla.trials.core.tipoprueba.TipoPruebasRepository;
import mx.gob.pjpuebla.trials.workflow.audiencias.Audiencia;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaService;
import mx.gob.pjpuebla.trials.workflow.audienciaspruebas.record.AudienciaPruebaRequestRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.DigitalizacionService;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DigitalizacionRecord;

import java.util.Optional;

class AudienciaPruebasServiceTest {

    @Mock
    private TipoPruebasRepository tipoPruebaRepository;

    @Mock
    private MateriaPericialRepository materiaPericialRepository;

    @Mock
    private AudienciaPruebasRepository audienciaPruebaRepository;

    @Mock
    private DigitalizacionService digitalizacionService;

    @Mock
    private DocumentoRepository documentoRepository;

    @Mock
    private AudienciaService audienciaService;

    @InjectMocks
    private AudienciaPruebasService audienciaPruebasService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAudienciaPrueba_WithValidInputs() {
        // Preparar datos de prueba
         AudienciaPruebaRequestRecord requestRecord = new AudienciaPruebaRequestRecord(
            1,
            2,
            "John Doe",
            "Descripción del instrumento",
            "Absolvente",
            "Descripción del documento",
            "Objeto del documento",
            "http://example.com/documento.pdf",
            1001 // idCarpeta
    );

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);

        Audiencia mockAudiencia = new Audiencia();
        Carpeta mockCarpeta = new Carpeta();
        mockCarpeta.setExpediente("000001/2025");
        when(audienciaService.obtenerUltimaAudienciaDesahogada()).thenReturn(mockAudiencia);

        TipoPruebas mockTipoPruebas = new TipoPruebas();
        when(tipoPruebaRepository.findById(1)).thenReturn(Optional.of(mockTipoPruebas));

        MateriaPericial mockMateriaPericial = new MateriaPericial();
        when(materiaPericialRepository.findById(2)).thenReturn(Optional.of(mockMateriaPericial));

        Documento mockDocumento = new Documento();
        when(documentoRepository.save(any(Documento.class))).thenReturn(mockDocumento);

        DigitalizacionRecord mockDigitalizacionRecord = new DigitalizacionRecord(1, "/opt/pjp/files/digitalizacion/", "archivo.pdf");

        when(digitalizacionService.guardarArchivo(file, mockDocumento.getId())).thenReturn(mockDigitalizacionRecord);

        AudienciaPruebas mockAudienciaPruebas = new AudienciaPruebas();
        when(audienciaPruebaRepository.save(any(AudienciaPruebas.class))).thenReturn(mockAudienciaPruebas);

        AudienciaPruebas result = audienciaPruebasService.createAudienciaPrueba(requestRecord, file);

        assertNotNull(result);
        verify(audienciaService).obtenerUltimaAudienciaDesahogada();
        verify(tipoPruebaRepository).findById(1);
        verify(materiaPericialRepository).findById(2);
        verify(documentoRepository).save(any(Documento.class));
        verify(digitalizacionService).guardarArchivo(file, mockDocumento.getId());
        verify(audienciaPruebaRepository).save(any(AudienciaPruebas.class));
    }
}

