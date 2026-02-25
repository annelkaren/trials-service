package mx.gob.pjpuebla.trials.workflow.audienciaspruebas;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import mx.gob.pjpuebla.trials.util.enums.DesistimientoAdmision;
import mx.gob.pjpuebla.trials.workflow.audienciaspruebas.record.DetallesPruebasRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import mx.gob.pjpuebla.trials.core.materiapericial.MateriaPericial;
import mx.gob.pjpuebla.trials.core.materiapericial.MateriaPericialRepository;
import mx.gob.pjpuebla.trials.core.tipoprueba.TipoPruebas;
import mx.gob.pjpuebla.trials.core.tipoprueba.TipoPruebasRepository;
import mx.gob.pjpuebla.trials.workflow.audiencias.Audiencia;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaRepository;
import mx.gob.pjpuebla.trials.workflow.audienciaspruebas.record.AudienciaPruebaRequestRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.DigitalizacionService;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DigitalizacionRecord;

import java.util.List;
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
    private AudienciaRepository audienciaRepository;

    @InjectMocks
    private AudienciaPruebasService audienciaPruebasService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetAudienciaPruebasByAudiencia() {
        Integer idAudiencia = 1;
        Pageable pageable = Pageable.unpaged();

        DetallesPruebasRecord prueba1 = new DetallesPruebasRecord(
                1,
                "Juan Pérez",
                "Pericial",
                "Descripción de prueba pericial",
                "Perito 1",
                DesistimientoAdmision.ADMISION
        );

        DetallesPruebasRecord prueba2 = new DetallesPruebasRecord(
                2,
                "Ana Gómez",
                "Testimonio",
                "Descripción del testimonio",
                "Testigo 1",
                DesistimientoAdmision.DESISTIMIENTO
        );

        List<DetallesPruebasRecord> listaPruebas = List.of(prueba1, prueba2);
        Page<DetallesPruebasRecord> pagePruebas = new PageImpl<>(listaPruebas);

        when(audienciaPruebaRepository.getAllByAudiencia(idAudiencia, pageable)).thenReturn(pagePruebas);

        Page<DetallesPruebasRecord> result = audienciaPruebasService.getAudienciaPruebasByAudiencia(idAudiencia, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());  // Asegurarnos que se devuelvan 2 pruebas

        DetallesPruebasRecord primeraPrueba = result.getContent().get(0);
        assertEquals(1, primeraPrueba.idAudienciaPruebas());
        assertEquals("Juan Pérez", primeraPrueba.asistente());
        assertEquals("Pericial", primeraPrueba.tipoPrueba());
        assertEquals("Descripción de prueba pericial", primeraPrueba.descripcion());
        assertEquals("Perito 1", primeraPrueba.tipoPerito());
        assertEquals(DesistimientoAdmision.ADMISION, primeraPrueba.desistimientoAdmision());

        DetallesPruebasRecord segundaPrueba = result.getContent().get(1);
        assertEquals(2, segundaPrueba.idAudienciaPruebas());
        assertEquals("Ana Gómez", segundaPrueba.asistente());
        assertEquals("Testimonio", segundaPrueba.tipoPrueba());
        assertEquals("Descripción del testimonio", segundaPrueba.descripcion());
        assertEquals("Testigo 1", segundaPrueba.tipoPerito());
        assertEquals(DesistimientoAdmision.DESISTIMIENTO, segundaPrueba.desistimientoAdmision());
    }

    @Test
    void testCreateAudienciaPrueba_WithValidInputs() {
         AudienciaPruebaRequestRecord requestRecord = new AudienciaPruebaRequestRecord(
            1,
            2,
            "John Doe",
            "Descripción del instrumento",
            "Absolvente",
            "Descripción del documento",
            "Objeto del documento",
            "http://example.com/documento.pdf",
            1001, // idCarpeta
            1 // Assuming 1 as a default value for the missing 10th Integer argument
    );

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);

        Audiencia mockAudiencia = new Audiencia();
        Carpeta mockCarpeta = new Carpeta();
        mockCarpeta.setExpediente("000001/2025");
        mockAudiencia.setId(requestRecord.audienciaId());
        mockAudiencia.setCarpeta(mockCarpeta);
        when(audienciaRepository.findById(requestRecord.audienciaId())).thenReturn(Optional.of(mockAudiencia));

        TipoPruebas mockTipoPruebas = new TipoPruebas();
        when(tipoPruebaRepository.findById(1)).thenReturn(Optional.of(mockTipoPruebas));

        MateriaPericial mockMateriaPericial = new MateriaPericial();
        when(materiaPericialRepository.findById(2)).thenReturn(Optional.of(mockMateriaPericial));

        Documento mockDocumento = new Documento();
        when(documentoRepository.save(any(Documento.class))).thenReturn(mockDocumento);

        DigitalizacionRecord mockDigitalizacionRecord = new DigitalizacionRecord(1, "/opt/pjp/files/digitalizacion/", "archivo.pdf");

        when(digitalizacionService.guardarDocumentoPruebaAudiencia(file, mockDocumento.getId(), requestRecord.audienciaId()))
                .thenReturn(mockDigitalizacionRecord);

        AudienciaPruebas mockAudienciaPruebas = new AudienciaPruebas();
        when(audienciaPruebaRepository.save(any(AudienciaPruebas.class))).thenReturn(mockAudienciaPruebas);

        AudienciaPruebas result = audienciaPruebasService.createAudienciaPrueba(requestRecord, file);

        assertNotNull(result);
        verify(audienciaRepository).findById(requestRecord.audienciaId());
        verify(tipoPruebaRepository).findById(1);
        verify(materiaPericialRepository).findById(2);
        verify(documentoRepository).save(any(Documento.class));
        verify(digitalizacionService).guardarDocumentoPruebaAudiencia(file, mockDocumento.getId(),
                requestRecord.audienciaId());
        verify(audienciaPruebaRepository).save(any(AudienciaPruebas.class));
    }


    @Test
    void testPachDesistimientoAdmision_ShouldUpdateCorrectly() {
        Integer id = 1;
        String desAdm = "DESISTIMIENTO=someValue";  // Un valor que simula el desistimiento

        AudienciaPruebas mockAudienciaPruebas = new AudienciaPruebas();
        when(audienciaPruebaRepository.findById(anyLong())).thenReturn(Optional.of(mockAudienciaPruebas));

        audienciaPruebasService.pachDesistimientoAdmision(desAdm, id);

        assertEquals(DesistimientoAdmision.DESISTIMIENTO, mockAudienciaPruebas.getDesistimientoAdmision());
        verify(audienciaPruebaRepository).save(mockAudienciaPruebas);  }
}

