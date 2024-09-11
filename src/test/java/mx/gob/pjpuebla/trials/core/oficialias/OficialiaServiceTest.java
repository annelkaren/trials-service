package mx.gob.pjpuebla.trials.core.oficialias;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.juzgados.*;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRecordResponse;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialia;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRecord;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRepository;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaSetUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OficialiaServiceTest {

    @InjectMocks
    OficialiaService oficialiaService;

    @Mock
    OficialiaRepository oficialiaRepository;

    @Mock
    TipoOficialiaRepository tipoOficialiaRepository;

    @Mock
    SedeRepository sedeRepository;

    @Mock
    private Pageable pageableMock;

    private Oficialia oficialia;
    private OficialiaRecord oficialiaRecord;

    @BeforeEach
    public void setUp() {
        TipoOficialia tipoOficialia = TipoOficialiaSetUp.createtipoOficialia();
        Distrito distrito = DistritoSetUp.createDistrito();
        Domicilio domicilio = DomicilioSetUp.createDomicilio();
        Sede sede = SedeSetUp.createSede();
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        oficialia = OficialiaSetUp.createOficialia(tipoOficialia, sede);
        oficialiaRecord = OficialiaSetUp.createOficialiaRecord(oficialia, new TipoOficialiaRecord(tipoOficialia.getId(), tipoOficialia.getNombre()), new SedeRecordResponse(sede.getId(),sede.getNombre(),sede.getEstado()));
    }

    @Test
    void getAll_return_page() {
        List<Oficialia> listPage = Collections.singletonList(oficialia);
        given(oficialiaRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<OficialiaRecord> page = oficialiaService.getAllActive(PageRequest.of(1, listPage.size()), oficialia);
        assertThat(page.getContent())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", oficialia.getId())
                .hasFieldOrPropertyWithValue("nombre", oficialia.getNombre());
    }
}