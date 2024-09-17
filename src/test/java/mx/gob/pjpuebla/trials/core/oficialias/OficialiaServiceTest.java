package mx.gob.pjpuebla.trials.core.oficialias;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRecordResponse;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialia;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRecord;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRepository;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaSetUp;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private TipoOficialia tipoOficialia;

    @BeforeEach
    public void setUp() {
        tipoOficialia = TipoOficialiaSetUp.createtipoOficialia();
        Distrito distrito = DistritoSetUp.createDistrito();
        Domicilio domicilio = DomicilioSetUp.createDomicilio();
        Sede sede = SedeSetUp.createSede();
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        oficialia = OficialiaSetUp.createOficialia(tipoOficialia, sede);
        oficialiaRecord = OficialiaSetUp.createOficialiaRecord(oficialia, new TipoOficialiaRecord(tipoOficialia.getId(), tipoOficialia.getNombre()), new SedeRecordResponse(sede.getId(), sede.getNombre(), sede.getEstado()));
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
                .hasFieldOrPropertyWithValue("version", oficialia.getVersion())
                .hasFieldOrPropertyWithValue("estado", oficialia.getEstado())
                .hasFieldOrPropertyWithValue("responsable", oficialia.getResponsable())
                .hasFieldOrPropertyWithValue("nombre", oficialia.getNombre());

        assertThat(oficialia.getTipoOficialia())
                .hasFieldOrPropertyWithValue("id", oficialia.getTipoOficialia().getId())
                .hasFieldOrPropertyWithValue("nombre", oficialia.getTipoOficialia().getNombre());

        assertThat(oficialia.getSede())
                .hasFieldOrPropertyWithValue("id", oficialia.getSede().getId())
                .hasFieldOrPropertyWithValue("nombre", oficialia.getSede().getNombre())
                .hasFieldOrPropertyWithValue("estado", oficialia.getSede().getEstado());
    }

    @Test
    void getById_return_oficialia() {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(oficialiaRepository.findByIdAndEstadoIn(oficialia.getId(), estados))
                .willReturn(Optional.ofNullable(oficialiaRecord));

        OficialiaRecord mr = oficialiaService.findById(oficialia.getId());
        assertThat(mr).isOfAnyClassIn(OficialiaRecord.class)
                .hasFieldOrPropertyWithValue("id", oficialia.getId())
                .hasFieldOrPropertyWithValue("nombre", oficialia.getNombre());
    }

    @Test
    void getById_return_not_found() {
        Integer personaId = oficialia.getId();
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(oficialiaRepository.findByIdAndEstadoIn(personaId, estados))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    oficialiaService.findById(personaId);
                }
        );

        assertThat(assertThrows.getMessage()).contains("Oficialia no encontrada");
    }

    @Test
    void create() {
        given(sedeRepository.findById(oficialia.getSede().getId()))
                .willReturn(Optional.ofNullable(oficialia.getSede()));
        given(tipoOficialiaRepository.findById(tipoOficialia.getId()))
                .willReturn(Optional.of(tipoOficialia));
        given(oficialiaRepository.save(oficialia))
                .willReturn(oficialia);

        OficialiaRecordResponse response = oficialiaService.create(oficialia);

        assertThat(response).isOfAnyClassIn(OficialiaRecordResponse.class)
                .hasFieldOrPropertyWithValue("id", oficialia.getId())
                .hasFieldOrPropertyWithValue("nombre", oficialia.getNombre());
    }

    @Test
    void update() {
        given(sedeRepository.findById(oficialia.getSede().getId()))
                .willReturn(Optional.ofNullable(oficialia.getSede()));
        given(tipoOficialiaRepository.findById(tipoOficialia.getId()))
                .willReturn(Optional.of(tipoOficialia));
        given(oficialiaRepository.save(oficialia))
                .willReturn(oficialia);

        OficialiaRecordResponse response = oficialiaService.update(oficialia);

        assertThat(response).isOfAnyClassIn(OficialiaRecordResponse.class)
                .hasFieldOrPropertyWithValue("id", oficialia.getId())
                .hasFieldOrPropertyWithValue("nombre", oficialia.getNombre());
    }

    @Test
    void update_return_optimistic_exception() {
        given(sedeRepository.findById(oficialia.getSede().getId()))
                .willReturn(Optional.ofNullable(oficialia.getSede()));
        given(tipoOficialiaRepository.findById(tipoOficialia.getId()))
                .willReturn(Optional.of(tipoOficialia));
        given(oficialiaRepository.save(oficialia))
                .willThrow(org.springframework.dao.OptimisticLockingFailureException.class);

        OptimisticLockingFailureException assertThrows = assertThrows(
                OptimisticLockingFailureException.class,
                () -> {
                    oficialiaService.update(oficialia);
                }
        );

        assertThat(assertThrows.getMessage()).contains("Oficialia modificada por otro usuario");
    }
}