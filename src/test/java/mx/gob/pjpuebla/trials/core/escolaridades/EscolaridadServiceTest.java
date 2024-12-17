package mx.gob.pjpuebla.trials.core.escolaridades;

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

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EscolaridadServiceTest {


    @Mock
    EscolaridadRepository escolaridadRepository;

    @InjectMocks
    EscolaridadService escolaridadService;

    private Escolaridad escolaridad;

    @BeforeEach
    public void setUp() {
        escolaridad = EscolaridadSetUp.createEscolaridad();
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAll_return_page() {
        List<Escolaridad> listPage = Collections.singletonList(escolaridad);
        given(escolaridadRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<EscolaridadRecord> page = escolaridadService.getAllActive(PageRequest.of(1, listPage.size()), escolaridad);
        assertThat(page.getContent())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", escolaridad.getId())
                .hasFieldOrPropertyWithValue("nombre", escolaridad.getNombre());
    }
}
