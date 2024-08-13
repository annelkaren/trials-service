package mx.gob.pjpuebla.trials.core.juzgado;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.util.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
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
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class JuzgadoServiceTest {

    @InjectMocks
    JuzgadoService service;

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

        BDDMockito.given(this.repository.findAll(Mockito.any(Pageable.class))).willReturn(juzgadoPaginado);
        PagedModel<Juzgado> pagedModel = new PagedModel<>(juzgadoPaginado);
        Response response = new Response();
        response.setData(juzgadoPaginado);
        Response response1 = this.service.getAll(pageableMock);

        Assertions.assertEquals("La solicitud se ha completado satisfactoriamente.", response1.getMessage());
        //Assertions.assertEquals(response, response1.getData());
    }

    private Juzgado crearJuzgado() {
        Juzgado juzgado = new Juzgado();
        juzgado.setId(new Random().nextInt());
        juzgado.setNombre(RandomStringUtils.random(5, true, true));
        return juzgado;
    }
}
