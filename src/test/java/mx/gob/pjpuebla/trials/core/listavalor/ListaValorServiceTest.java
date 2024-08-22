package mx.gob.pjpuebla.trials.core.listavalor;

import mx.gob.pjpuebla.trials.util.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
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
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ListaValorServiceTest {

    @Mock
    public ListaValorRepository listaValorRepository;

    @InjectMocks
    public ListaValorService listaValorService;

    @Mock
    private Pageable pageableMock;

    @DisplayName("Should return a response with a paginated list of ListaValor items")
    @Test
    void getAll() {
        PageRequest paginator = PageRequest.of(1, 10);
        List<ListaValor> list = new ArrayList<>();
        list.add(createListaValor());
        list.add(createListaValor());
        list.add(createListaValor());
        list.add(createListaValor());
        Page<ListaValor> listaValorPage = new PageImpl<>(list, paginator, list.size());
        given(listaValorRepository.findAll(Mockito.any(Pageable.class))).willReturn(listaValorPage);

        PagedModel<ListaValor> expected = new PagedModel<>(listaValorPage);
        Response response = listaValorService.getAll(pageableMock);

        assertThat(response.getMessage()).isNotNull();
        assertThat(response.getData()).isNotNull();
        assertThat(expected).isEqualTo(response.getData());
    }

    @Test
    void findById() {
        ListaValor entity = createListaValor();
        given(listaValorRepository.findById(entity.getId())).willReturn(Optional.of(entity));

        Response response = listaValorService.findById(entity.getId());

        assertThat(response.getMessage()).isNotNull();
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData()).isEqualTo(entity);
    }

    @DisplayName("Should return an error when id does not exist")
    @Test
    void findByIdError() {
        int fakeId = new Random().nextInt();
        given(listaValorRepository.findById(Mockito.any(Integer.class))).willReturn(null);
        Response response = listaValorService.findById(fakeId);

        assertThat(response.getMessage()).isNotNull();
        assertThat(response.getMessage()).startsWith("Error");
        assertThat(response.getData()).isNull();
    }

    @Test
    void create() {
        ListaValor entity = createListaValor();
        given(listaValorRepository.save(entity)).willReturn(entity);

        Response response = listaValorService.create(entity);

        assertThat(response.getMessage()).isNotNull();
        assertThat(response.getMessage()).contains(entity.getId().toString());
    }

    @DisplayName("Should return a message when the repository throws an OptimisticLocking exception")
    @Test
    void update() {
        ListaValor entity = createListaValor();
        given(listaValorRepository.save(entity)).willThrow(OptimisticLockingFailureException.class);

        Response response = listaValorService.update(entity);

        assertThat(response.getMessage()).isNotNull();
        assertThat(response.getMessage()).isEqualTo("El registro fue actualizado o eliminado por otra transaccion");
    }

    @Test
    void delete() {
        ListaValor entity = createListaValor();
        Response response = listaValorService.delete(entity.getId());

        assertThat(response.getMessage()).isNotNull();
        assertThat(response.getMessage()).contains(entity.getId().toString());
    }

    private ListaValor createListaValor() {
        return ListaValor.builder()
                .id(new Random().nextInt())
                .estado("A")
                .nombre(RandomStringUtils.random(5, true, true))
                .build();
    }
}
