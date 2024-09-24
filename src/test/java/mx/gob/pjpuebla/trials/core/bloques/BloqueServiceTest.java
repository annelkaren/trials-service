package mx.gob.pjpuebla.trials.core.bloques;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class BloqueServiceTest {

    @Mock
    private BloqueRepository mockBloqueRepository;

    @InjectMocks
    private BloqueService bloqueService;

    private Bloque bloque;

    @BeforeEach
    public void setUp() {
        bloque = BloqueSetUp.createBloque();
        bloque.setHoraInicial(LocalTime.of(8, 30));
        bloque.setHoraFinal(LocalTime.of(9, 30));
    }

    @Test
    void getAll_return_page_whenHoraInicialNotNull() {
       
        List<Bloque> listPage = Collections.singletonList(bloque);
        given(mockBloqueRepository.findByHoraInicial(any(LocalTime.class), any(PageRequest.class)))
            .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));

        Page<BloqueRecord> page = bloqueService.getAll(bloque, PageRequest.of(0, listPage.size()));

        assertThat(page.getContent())
            .hasSize(1)
            .first().hasFieldOrPropertyWithValue("id", bloque.getId())
            .hasFieldOrPropertyWithValue("horaInicial", bloque.getHoraInicial())
            .hasFieldOrPropertyWithValue("horaFinal", bloque.getHoraFinal());
    }

    @Test
    void getAll_return_page_whenHoraInicialIsNull() {
       
        List<Bloque> listPage = Collections.singletonList(bloque);
        given(mockBloqueRepository.findAll(any(PageRequest.class)))
            .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));

        Page<BloqueRecord> page = bloqueService.getAll(new Bloque(), PageRequest.of(0, listPage.size()));

        assertThat(page.getContent())
            .hasSize(1)
            .first().hasFieldOrPropertyWithValue("id", bloque.getId())
            .hasFieldOrPropertyWithValue("horaInicial", bloque.getHoraInicial())
            .hasFieldOrPropertyWithValue("horaFinal", bloque.getHoraFinal());
    }
}
