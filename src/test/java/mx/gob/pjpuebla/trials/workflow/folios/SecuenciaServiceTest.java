package mx.gob.pjpuebla.trials.workflow.folios;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class SecuenciaServiceTest {

    @Mock
    SecuenciaRepository mockSecuenciaRepository;

    @InjectMocks
    SecuenciaService target;

    @ParameterizedTest
    @ValueSource(strings = {"E", "D", "P"})
    void getIdBySecuences_success(String tipo) {
        String result = target.obtenerFolio(tipo);
        assertThat(result)
                .startsWith(tipo)
                .isNotBlank()
                .isGreaterThan(tipo);
    }

    @Test
    void getIdOptionNotExist_failure() {
        String tipo = "X";
        IllegalArgumentException assertThrows = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    target.obtenerFolio(tipo);
                }
        );
        assertThat(assertThrows.getMessage()).contains("Tipo de folio no válido: " + tipo);
    }

    @Test
    void getIdWithNullCase_failure() {
        String tipo = "";
        IllegalArgumentException assertThrows = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    target.obtenerFolio(tipo);
                }
        );
        assertThat(assertThrows.getMessage()).contains("Tipo de folio no válido: ");
    }

}