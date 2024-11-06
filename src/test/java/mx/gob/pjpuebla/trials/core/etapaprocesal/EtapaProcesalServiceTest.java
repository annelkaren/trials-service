package mx.gob.pjpuebla.trials.core.etapaprocesal;

import mx.gob.pjpuebla.trials.core.etapaprocesal.record.EtapaProcesalRecord;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import mx.gob.pjpuebla.trials.error.NotFoundException;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EtapaProcesalServiceTest {

    @InjectMocks
    EtapaProcesalService etapaProcesalService;
    @Mock
    EtapaProcesalRepository etapaProcesalRepository;
    @Mock
    TipoJuicioRepository tipoJuicioRepository;

    @Test
    void getEtapaProcesal_return_list1() {
        EtapaProcesalRecord etapaProcesalRecord = EtapaProcesalSetUp.createEtapaProcesalRecord();
        List<EtapaProcesalRecord> listRecord = Collections.singletonList(etapaProcesalRecord);

        given(etapaProcesalRepository.getListEtapaProcesalByTipoJuicioAndProcedimiento(1, 1, null))
                .willReturn(listRecord);

        TipoJuicio tipoJuicioSimulado = TipoJuicioSetUp.createTipoJuicio();
        given(tipoJuicioRepository.getMateriaAndTipoSistemaById(1))
                .willReturn(java.util.Optional.of(tipoJuicioSimulado));

        List<EtapaProcesalRecord> resultList = etapaProcesalService.getEtapaProcesal(1, 0);

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals(etapaProcesalRecord, resultList.get(0));
    }

    @Test
    void getEtapaProcesal_return_emply() {
        given(etapaProcesalRepository.getListEtapaProcesalByTipoJuicioAndProcedimiento(1, 1, null))
                .willReturn(null);

        TipoJuicio tipoJuicioSimulado = TipoJuicioSetUp.createTipoJuicio();
        given(tipoJuicioRepository.getMateriaAndTipoSistemaById(1))
                .willReturn(java.util.Optional.of(tipoJuicioSimulado));

        List<EtapaProcesalRecord> resultList = etapaProcesalService.getEtapaProcesal(1, 0);
        assertThat(resultList).isNull();
    }

    @Test
    void getEtapaProcesal_tipoJuicio_not_found() {
        given(tipoJuicioRepository.getMateriaAndTipoSistemaById(anyInt()))
                .willReturn(java.util.Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> etapaProcesalService.getEtapaProcesal(1, 0));
        assertTrue(exception.getMessage().contains("Tipo de Juicio no encontrado"));
        assertTrue(exception.getMessage().contains("404 NOT_FOUND"));
    }

}