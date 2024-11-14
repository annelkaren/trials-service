package mx.gob.pjpuebla.trials.core.documentoidentificacion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class DocumentoIdentificacionServiceTest {

    @InjectMocks
    DocumentoIdentificacionService documentoIdentificacionService;

    @Mock
    DocumentoIdentificacionRepository documentoIdentificacionRepository;

    @Test
    void testGetAll() {
        List<DocumentoIdentificacion> identificacionesMock = DocumentoIdentificacionSetUp.createIdentificaciones();
        when(documentoIdentificacionRepository.findAll()).thenReturn(identificacionesMock);
        List<IdentificacionDocRecord> result = documentoIdentificacionService.getAll();

        assertEquals(2, result.size(), "Debería devolver 2 registros");

        assertEquals(1, result.get(0).id(), "El ID del primer registro debería ser 1");
        assertEquals("Gafete Institucional defensoría pública", result.get(0).identificacion(),
                "La identificación del primer registro debería ser 'Gafete Institucional defensoría pública'");

        assertEquals(2, result.get(1).id(), "El ID del segundo registro debería ser 2");
        assertEquals("Gafete Institucional centro de meditación", result.get(1).identificacion(),
                "La identificación del segundo registro debería ser 'Carnet de identificación funcionario público'");
    }
}