package mx.gob.pjpuebla.trials.core.bloques;

import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BloqueServiceTest {

    @Mock
    private BloqueRepository mockBloqueRepository;

    @InjectMocks
    private BloqueService bloqueService;

    private Bloque bloque;
    private BloqueRecordResponse bloqueRecordResponse;

    @BeforeEach
    public void setUp() {
        bloque = BloqueSetUp.createBloque();
        bloqueRecordResponse = BloqueSetUp.createBloqueRecordResponse();
        bloque.setHoraInicial(LocalTime.of(8, 30));
        bloque.setHoraFinal(LocalTime.of(9, 30));
    }

    @Test
    void getAll_return_page_whenHoraInicialNotNull() {
       
        List<Bloque> listPage = Collections.singletonList(bloque);
        given(mockBloqueRepository.findByHoraInicial(any(LocalTime.class), any(PageRequest.class)))
            .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));

        Page<BloqueRecordResponse> page = bloqueService.getAll(bloque, PageRequest.of(0, listPage.size()));

        assertThat(page.getContent())
            .hasSize(1)
            .first().hasFieldOrPropertyWithValue("id", bloque.getId())
            .hasFieldOrPropertyWithValue("horaInicial", bloque.getHoraInicial())
            .hasFieldOrPropertyWithValue("horaFinal", bloque.getHoraFinal())
            .hasFieldOrPropertyWithValue("estado", bloque.getEstado());
    }

    @Test
    void getAll_return_page_whenHoraInicialIsNull() {
       
        List<Bloque> listPage = Collections.singletonList(bloque);
        given(mockBloqueRepository.findAll(any(PageRequest.class)))
            .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));

        Page<BloqueRecordResponse> page = bloqueService.getAll(new Bloque(), PageRequest.of(0, listPage.size()));

        assertThat(page.getContent())
            .hasSize(1)
            .first().hasFieldOrPropertyWithValue("id", bloque.getId())
            .hasFieldOrPropertyWithValue("horaInicial", bloque.getHoraInicial())
            .hasFieldOrPropertyWithValue("horaFinal", bloque.getHoraFinal())
            .hasFieldOrPropertyWithValue("estado", bloque.getEstado());
    }

    @Test
    void getById_return_bloqueRecord() {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(mockBloqueRepository.findByIdAndEstadoIn(bloque.getId(), estados))
                .willReturn(Optional.ofNullable(bloqueRecordResponse));

        BloqueRecordResponse result = bloqueService.findById(bloque.getId());
        assertThat(result).isOfAnyClassIn(BloqueRecordResponse.class)
                .hasFieldOrPropertyWithValue("id", bloque.getId())
                .hasFieldOrPropertyWithValue("horaInicial", bloque.getHoraInicial())
                .hasFieldOrPropertyWithValue("horaFinal", bloque.getHoraFinal())
                .hasFieldOrPropertyWithValue("estado", bloque.getEstado());
    }

    @Test
    void getById_return_not_found() {
        Integer id = bloque.getId();
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(mockBloqueRepository.findByIdAndEstadoIn(bloque.getId(), estados))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> bloqueService.findById(id)
        );

        assertThat(assertThrows.getMessage()).contains("Bloque no encontrado");
    }

    @Test
    void create() {
        given(mockBloqueRepository.save(bloque))
            .willReturn(bloque);
        BloqueRecordResponse response = bloqueService.create(bloque);

        assertThat(response).isOfAnyClassIn(BloqueRecordResponse.class)
            .hasFieldOrPropertyWithValue("id", bloque.getId())
            .hasFieldOrPropertyWithValue("horaInicial", bloque.getHoraInicial())
            .hasFieldOrPropertyWithValue("horaFinal", bloque.getHoraFinal())
            .hasFieldOrPropertyWithValue("estado", bloque.getEstado());
    }

    @Test
    void update() {
        given(mockBloqueRepository.save(bloque))
                .willReturn(bloque);
        BloqueRecordResponse response = bloqueService.update(bloque);

        assertThat(response).isOfAnyClassIn(BloqueRecordResponse.class)
                .hasFieldOrPropertyWithValue("id", bloque.getId())
                .hasFieldOrPropertyWithValue("horaInicial", bloque.getHoraInicial())
                .hasFieldOrPropertyWithValue("horaFinal", bloque.getHoraFinal())
                .hasFieldOrPropertyWithValue("estado", bloque.getEstado());
    }

    @Test
    void update_invalid_version_exception() {
        given(mockBloqueRepository.save(bloque)).willThrow(org.springframework.dao.OptimisticLockingFailureException.class);

        InvalidVersionException assertThrows = assertThrows(
                InvalidVersionException.class,
                () -> bloqueService.update(bloque)
        );

        assertThat(assertThrows.getMessage()).contains("Version modificada por otro usuario");
    }
}
