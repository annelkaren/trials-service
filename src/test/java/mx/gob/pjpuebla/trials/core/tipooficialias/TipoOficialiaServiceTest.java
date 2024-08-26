package mx.gob.pjpuebla.trials.core.tipooficialias;

import mx.gob.pjpuebla.trials.util.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TipoOficialiaServiceTest {

    @Mock
    public TipoOficialiasRepository tipoOficialiasRepository;

    @InjectMocks
    public TipoOficialiaService tipoOficialiaService;

    @Mock
    private Pageable pageableMock;

    @DisplayName("Should return a response with a paginated list of TipoOficialias items")
    @Test
    void getAll() {
        PageRequest paginator = PageRequest.of(1, 10);
        List<TipoOficialias> list = new ArrayList<>();
        list.add(createTipoOficialias());
        list.add(createTipoOficialias());
        list.add(createTipoOficialias());
        list.add(createTipoOficialias());
        Page<TipoOficialias> tipoOficialiasPage = new PageImpl<>(list, paginator, list.size());
        given(tipoOficialiasRepository.findAll(Mockito.any(Pageable.class))).willReturn(tipoOficialiasPage);

        List<TipoOficialias> expected = tipoOficialiasPage.getContent();
        List<TipoOficialias> result = tipoOficialiaService.getAll(pageableMock);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(expected.size());
        assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }

    private TipoOficialias createTipoOficialias(){
        return  TipoOficialias.builder()
                .id(new Random().nextInt())
                .version(1)
                .nombre("Común")
                .estado("A")
                .build();
    }

}