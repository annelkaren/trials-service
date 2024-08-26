package mx.gob.pjpuebla.trials.core.tiposistema;

import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialias;
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
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TipoSistemaServiceTest {

    @Mock
    public TipoSistemaRepository tipoSistemaRepository;

    @InjectMocks
    public TipoSistemaService tipoSistemaService;

    @Mock
    private Pageable pageableMock;

    @DisplayName("Should return a response with a paginated list of TipoSistema items")
    @Test
    void getAll() {
        PageRequest paginator = PageRequest.of(1, 10);
        List<TipoSistema> list = new ArrayList<>();
        list.add(createTipoSistema());
        list.add(createTipoSistema());
        list.add(createTipoSistema());
        list.add(createTipoSistema());
        Page<TipoSistema> tipoSistemaPage = new PageImpl<>(list, paginator, list.size());
        given(tipoSistemaRepository.findAll(Mockito.any(Pageable.class))).willReturn(tipoSistemaPage);

        List<TipoSistema> expected = tipoSistemaPage.getContent();
        List<TipoSistema> result = tipoSistemaService.getAll(pageableMock);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(expected.size());
        assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }


    private TipoSistema  createTipoSistema(){
        return TipoSistema.builder()
                .id(new Random().nextInt())
                .version(1)
                .nombre("Mixto")
                .estado("A")
                .build();
    }
}