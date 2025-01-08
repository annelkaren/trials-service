package mx.gob.pjpuebla.trials.core.derechoshumanos;

import mx.gob.pjpuebla.trials.util.enums.TipoDerechosHumanos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DerechosHumanosServiceTest {

    @InjectMocks
    DerechosHumanosService derechosHumanosService;

    @Mock
    DerechosHumanosRepository derechosHumanosRepository;

    private DerechosHumanos derechosHumanos;
    private DerechosHumanosRecord derechosHumanosRecord;

    @BeforeEach
    void setUp() {
        derechosHumanos = new DerechosHumanos();
        derechosHumanos.setId(1);
        derechosHumanos.setNombre("Derecho a la vida");
        derechosHumanos.setTipoDerecho(TipoDerechosHumanos.DERECHOS_PERSONA);

        derechosHumanosRecord = new DerechosHumanosRecord(1, "Derecho a la vida", TipoDerechosHumanos.DERECHOS_PERSONA);
    }

    @Test
    void testGetListByTipo_Success() {
        given(derechosHumanosRepository.findByTipoDerecho(TipoDerechosHumanos.DERECHOS_PERSONA))
                .willReturn(Collections.singletonList(derechosHumanos));

        List<DerechosHumanosRecord> resultado = derechosHumanosService.getListByTipo("DERECHOS_PERSONA");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(derechosHumanosRecord.id(), resultado.get(0).id());
        assertEquals(derechosHumanosRecord.derechoHumano(), resultado.get(0).derechoHumano());
        assertEquals(derechosHumanosRecord.tipoDerechoHumano(), resultado.get(0).tipoDerechoHumano());
    }

    @Test
    void testGetListByTipo_EmptyList() {
        given(derechosHumanosRepository.findByTipoDerecho(TipoDerechosHumanos.DERECHOS_PERSONA))
                .willReturn(Collections.emptyList());

        List<DerechosHumanosRecord> resultado = derechosHumanosService.getListByTipo("DERECHOS_PERSONA");

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    @Test
    void testGetListByTipo_InvalidTipo() {
        String tipoInvalido = "INVALIDO";

        try {
            derechosHumanosService.getListByTipo(tipoInvalido);
        } catch (IllegalArgumentException e) {
            assertEquals("No enum constant mx.gob.pjpuebla.trials.util.enums.TipoDerechosHumanos." + tipoInvalido, e.getMessage());
        }
    }
}
