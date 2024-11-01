package mx.gob.pjpuebla.trials.workflow.sello;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import mx.gob.pjpuebla.trials.util.enums.Rol;

import java.util.Arrays;

public class SelloCaratulaServiceTest {

    @Mock
    private PersonaDocumentoRepository personaDocumentoRepository;

    @InjectMocks
    private SelloCaratulaService selloCaratulaService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetNombrePersonaByIdAndParte_Fisica() {
        Integer id = 1;
        String parte = "Actor";

        PersonaDocumentoRecord personaMock = mock(PersonaDocumentoRecord.class);
        when(personaMock.nombre()).thenReturn("John");
        when(personaMock.apellidoPaterno()).thenReturn("Doe");
        when(personaMock.apellidoMaterno()).thenReturn("Smith");
        when(personaMock.tipoPersona()).thenReturn("FISICA");

        when(personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(id, parte, Arrays.asList(Rol.PRINCIPAL))).thenReturn(personaMock);

        String resultado = selloCaratulaService.getNombrePersonaByIdAndParte(id, parte);

        assertEquals("John Doe Smith", resultado);
    }

    @Test
    public void testGetNombrePersonaByIdAndParte_Moral() {

        Integer id = 2;
        String parte = "Actor";

        PersonaDocumentoRecord personaMock = mock(PersonaDocumentoRecord.class);
        when(personaMock.nombre()).thenReturn("Empresa");
        when(personaMock.apellidoPaterno()).thenReturn(null);
        when(personaMock.apellidoMaterno()).thenReturn(null);
        when(personaMock.tipoPersona()).thenReturn("MORAL");

        when(personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(id, parte, Arrays.asList(Rol.PRINCIPAL)))
                .thenReturn(personaMock);

        String resultado = selloCaratulaService.getNombrePersonaByIdAndParte(id, parte);

        assertEquals("Empresa", resultado);
    }

    @Test
     void testGetExhortoPromocion_Exhorto() {
        TipoJuicio tipoJuicio = TipoJuicioSetUp.createTipoJuicio();
        Documento documento = DocumentoSetUp.create(tipoJuicio);
        Carpeta carpeta = CarpetaSetUp.createCarpetaExhorto();
        documento.setCarpeta(carpeta);

        DocumentoData data = new DocumentoData();
        data.setExhortoProcedencia("Procedencia Exhorto");
        documento.setData(data);

        String resultado = selloCaratulaService.getExhortoPromocion(documento);

        assertEquals("Procedencia Exhorto", resultado);
    }

    @Test
    void testGetExhortoPromocion_NoExhorto() {
        TipoJuicio tipoJuicio = TipoJuicioSetUp.createTipoJuicio();
        Documento documento = DocumentoSetUp.create(tipoJuicio);
        Carpeta carpeta = CarpetaSetUp.create();
        documento.setCarpeta(carpeta);

        DocumentoData data = new DocumentoData();
        data.setExhortoProcedencia("Procedencia Otro Tipo");
        documento.setData(data);

        String resultado = selloCaratulaService.getExhortoPromocion(documento);

        assertEquals("", resultado);
    }

    @Test
     void testGetExhortoPromocion_NullData() {
        TipoJuicio tipoJuicio = TipoJuicioSetUp.createTipoJuicio();
        Documento documento = DocumentoSetUp.create(tipoJuicio);
        Carpeta carpeta = CarpetaSetUp.createCarpetaExhorto();
        documento.setCarpeta(carpeta);

        DocumentoData data = new DocumentoData();
        documento.setData(data);

        String resultado = selloCaratulaService.getExhortoPromocion(documento);

        assertNull(resultado);
    }



}
