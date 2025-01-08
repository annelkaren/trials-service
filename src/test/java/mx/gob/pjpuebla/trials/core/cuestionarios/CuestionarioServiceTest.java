package mx.gob.pjpuebla.trials.core.cuestionarios;

import mx.gob.pjpuebla.trials.util.enums.ListCuestionario;
import mx.gob.pjpuebla.trials.util.enums.TipoPregunta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CuestionarioServiceTest {

    @InjectMocks
    CuestionarioService cuestionarioService;

    @Mock
    CuestionarioRepository cuestionarioRepository;

    private Cuestionario cuestionario;
    private CuestionarioRecord cuestionarioRecord;

    @BeforeEach
    void setUp() {
        cuestionario = new Cuestionario();
        cuestionario.setId(1);
        cuestionario.setPreguntas("¿La sentencia fue dictada por un órgano jurisdiccional auxiliar?");
        cuestionario.setLista(ListCuestionario.LISTA_SALAS);
        cuestionario.setTipo(TipoPregunta.SI_NO);

        cuestionarioRecord = new CuestionarioRecord(
                cuestionario.getId(),
                cuestionario.getPreguntas(),
                cuestionario.getLista(),
                cuestionario.getTipo()
        );
    }

    @Test
    void testGetListByLista_success() {
        given(cuestionarioRepository.findByLista(ListCuestionario.LISTA_SALAS_PENALES)).willReturn(Collections.singletonList(cuestionario));

        List<CuestionarioRecord> cuestionarios = cuestionarioService.getListByLista(1);

        assertNotNull(cuestionarios);
        assertEquals(1, cuestionarios.size());
        assertEquals(cuestionarioRecord, cuestionarios.get(0));
    }


    @Test
    void testGetListByLista_invalidIndex() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cuestionarioService.getListByLista(99);
        });

        assertEquals("Índice fuera de rango: 99", exception.getMessage());
    }

    @Test
    void testGetListByLista_nullIndex() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cuestionarioService.getListByLista(null); // Índice nulo
        });

        assertEquals("Índice fuera de rango: null", exception.getMessage());
    }
}
