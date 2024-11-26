package mx.gob.pjpuebla.trials.core.paises;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaisServiceTest {


    @InjectMocks
    PaisService paisService;

    @Mock
    PaisRepository paisRepository;



    @Test
    void testGetAll() {
        Pais pais1 = new Pais();
        pais1.setCca2("MX");
        pais1.setNombreComun("México");
        pais1.setNombreOficial("Estados Unidos Mexicanos");
        pais1.setId(2);

        Pais pais2 = new Pais();
        pais2.setCca2("US");
        pais2.setNombreComun("Estados Unidos");
        pais2.setNombreOficial("Estados Unidos de América");
        pais2.setId(1);

        when(paisRepository.findAll()).thenReturn(Arrays.asList(pais1, pais2));

        List<PaisRecord> result = paisService.getAll();

        assertEquals(2, result.size());

        assertEquals("MX", result.get(0).codeAlpha2());
        assertEquals("México", result.get(0).nombre());
        assertNotNull(result.get(0).codeNumeric());
        assertEquals("US", result.get(1).codeAlpha2());
        assertEquals("Estados Unidos", result.get(1).nombre());
        assertNotNull(result.get(1).codeNumeric());
    }
}