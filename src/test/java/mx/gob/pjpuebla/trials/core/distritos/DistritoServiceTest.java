package mx.gob.pjpuebla.trials.core.distritos;

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
class DistritoServiceTest {

    @Mock
    DistritoRepository mockDistritoRepository;

    @InjectMocks
    DistritoService target;

    private Distrito distrito;

    @BeforeEach
    public void setUp() {
        distrito = DistritoSetUp.createDistrito();
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAll_return_page() {
        List<Distrito> listPage = Collections.singletonList(distrito);
        given(mockDistritoRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<DistritoRecord> page = target.getAllActive(PageRequest.of(1, listPage.size()));
        assertThat(page.getContent())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", distrito.getId())
                .hasFieldOrPropertyWithValue("nombre", distrito.getNombre());
    }
}
