package mx.gob.pjpuebla.trials.core.sedes;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException;
import mx.gob.pjpuebla.trials.util.Estado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SedeServiceTest {

    @Mock
    SedeRepository mockSedeRepository;

    @Mock
    DistritoRepository mockDistritoRepository;

    @Mock
    DomicilioRepository mockDomicilioRepository;

    @InjectMocks
    SedeService sedeService;

    @Mock
    DomicilioService domicilioService;

    private Sede sede;
    private SedeRecord sedeRecord;
    private Distrito distrito;
    private Domicilio domicilio;

    @BeforeEach
    public void setUp() {
        sede = SedeSetUp.createSede(Estado.ACTIVE);
        sedeRecord = SedeSetUp.sedeRecord();
        distrito = DistritoSetUp.createDistrito();
        domicilio = DomicilioSetUp.createDomicilio();
    }

    @Test
    void getAll_return_page() {
        List<Sede> listPage = Collections.singletonList(sede);
        given(mockSedeRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<SedeRecordResponse> page = sedeService.getAll(sede, PageRequest.of(1, listPage.size()));
        assertThat(page.getContent())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", sede.getId())
                .hasFieldOrPropertyWithValue("nombre", sede.getNombre());
    }

    @Test
    void getById_return_sedeRecord() {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(mockSedeRepository.findByIdAndEstadoIn(sede.getId(), estados))
                .willReturn(Optional.ofNullable(sedeRecord));

        SedeRecord result = sedeService.findById(sede.getId());
        assertThat(result).isOfAnyClassIn(SedeRecord.class)
                .hasFieldOrPropertyWithValue("id", sede.getId())
                .hasFieldOrPropertyWithValue("nombre", sede.getNombre());
    }

    @Test
    void getById_return_not_found() {
        Integer id = sede.getId();
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(mockSedeRepository.findByIdAndEstadoIn(sede.getId(), estados))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    sedeService.findById(id);
                }
        );

        assertThat(assertThrows.getMessage()).contains("Sede no encontrada");
    }

    @Test
    void create() {
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        given(mockDistritoRepository.findById(distrito.getId()))
                .willReturn(Optional.ofNullable(distrito));
        given(domicilioService.save(domicilio))
                .willReturn(domicilio);
        given(mockSedeRepository.save(sede))
                .willReturn(sede);

        SedeRecordResponse response = sedeService.create(sede);

        assertThat(response).isOfAnyClassIn(SedeRecordResponse.class)
                .hasFieldOrPropertyWithValue("id", sede.getId())
                .hasFieldOrPropertyWithValue("nombre", sede.getNombre())
                .hasFieldOrPropertyWithValue("estado", sede.getEstado());
    }

    @Test
    void update() {
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        given(mockDistritoRepository.findById(distrito.getId()))
                .willReturn(Optional.ofNullable(distrito));
        given(domicilioService.save(domicilio))
                .willReturn(domicilio);
        given(mockSedeRepository.save(sede))
                .willReturn(sede);

        SedeRecordResponse response = sedeService.update(sede);

        assertThat(response).isOfAnyClassIn(SedeRecordResponse.class)
                .hasFieldOrPropertyWithValue("id", sede.getId())
                .hasFieldOrPropertyWithValue("nombre", sede.getNombre())
                .hasFieldOrPropertyWithValue("estado", sede.getEstado());
    }

    @Test
    void update_return_optimistic_exception() {
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        sede.setVersion(8);
        given(mockDistritoRepository.findById(distrito.getId()))
                .willReturn(Optional.ofNullable(distrito));
        given(domicilioService.save(domicilio))
                .willReturn(domicilio);
        given(mockSedeRepository.save(sede)).willThrow(org.springframework.dao.OptimisticLockingFailureException.class);

        OptimisticLockingFailureException assertThrows = assertThrows(
                OptimisticLockingFailureException.class,
                () -> {
                    sedeService.update(sede);
                }
        );

        assertThat(assertThrows.getMessage()).contains("Sede modificada por otro usuario");
    }
}
