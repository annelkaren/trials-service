package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.util.Response;
import org.apache.commons.lang3.RandomStringUtils;
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
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class JuzgadoServiceTest {

    @InjectMocks
    JuzgadoService juzgadoService;

    @Mock
    JuzgadoRepository repository;

    @Mock
    private Pageable pageableMock;

    @Test
    void testGetAll() {
        PageRequest paginator = PageRequest.of(1, 3);
        List<Juzgado> list = new ArrayList<>();
        list.add(crearJuzgado());
        list.add(crearJuzgado());
        list.add(crearJuzgado());
        list.add(crearJuzgado());
        Page<Juzgado> juzgadoPaginado = new PageImpl<>(list, paginator, list.size());

        given(repository.findAll(Mockito.any(Pageable.class))).willReturn(juzgadoPaginado);
        PagedModel<Juzgado> pagedModel = new PagedModel<>(juzgadoPaginado);
        Response response = juzgadoService.getAll(pageableMock);

        assertThat(response.getMessage()).isNotNull();
        assertThat(response.getData()).isNotNull();
        assertThat(pagedModel).isEqualTo(response.getData());
    }

    private Juzgado crearJuzgado() {
        Juzgado juzgado = new Juzgado();
        juzgado.setId(new Random().nextInt());
        juzgado.setNombre(RandomStringUtils.random(5, true, true));
        return juzgado;
    }

    @Test
    void findById() {
        given(repository.findById(anyInt())).willReturn(Optional.of(crearJuzgado()));
        Response response = juzgadoService.findById(Integer.MAX_VALUE);
        assertThat(response.getMessage()).isNotNull();
        assertThat(response.getData()).isNotNull();
    }
}
