package mx.gob.pjpuebla.trials.workflow.etiquetas;

import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EtiquetaServiceTest {

    @Mock
    public EtiquetaRepository etiquetaRepository;

    @InjectMocks
    public EtiquetaService etiquetaService;

    @Test
    void getAllByTipoJuicioId() {
        Etiqueta item = EtiquetaSetUp.createEtiqueta(100);
        List<Etiqueta> list = Arrays.asList(item);
        given(etiquetaRepository.findByTipoJuicioId(anyInt())).willReturn(list);

        EtiquetaRecordItem record = new EtiquetaRecordItem(item.getNombre(), item.getValue());
        List<EtiquetaRecordItem> results = etiquetaService.getAllByTipoJuicioId(100);

        assertThat(results).isNotNull();
        assertThat(results.size()).isPositive();
        assertThat(results.get(0)).isEqualTo(record);
    }

    @Test
    void renderEtiquetaRecepcion_promocion() {
        Documento promocion = new Documento().setTipoDocumento(TipoDocumento.PROMOCION);

        String results = etiquetaService.renderEtiquetaRecepcion("", promocion);

        assertThat(results).isNotNull();
        assertThat(results).isEqualTo(TipoDocumento.PROMOCION.name());
    }

    @Test
    void renderEtiquetaRecepcion_exhorto() {
        Carpeta carpeta = new Carpeta().setTipoCarpeta(TipoCarpeta.EXHORTO);
        Documento exhorto = new Documento().setCarpeta(carpeta);

        String results = etiquetaService.renderEtiquetaRecepcion("", exhorto);

        assertThat(results).isNotNull();
        assertThat(results).isEqualTo(TipoCarpeta.EXHORTO.name());
    }

    @Test
    void renderEtiquetaRecepcion_demanda() {
        Etiqueta item = EtiquetaSetUp.createEtiqueta(100);
        Carpeta carpeta = new Carpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        Documento demanda = new Documento().setCarpeta(carpeta);
        given(etiquetaRepository.findByTipoJuicioIdAndNombre(any(Integer.class), any(String.class))).willReturn(item);

        String results = etiquetaService.renderEtiquetaRecepcion("documento", demanda);

        assertThat(results).isNotNull();
        assertThat(results).isEqualTo(item.getValue().toUpperCase());
    }
}
