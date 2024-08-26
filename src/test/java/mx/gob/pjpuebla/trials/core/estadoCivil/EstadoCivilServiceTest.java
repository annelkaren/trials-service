package mx.gob.pjpuebla.trials.core.estadoCivil;

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
public class EstadoCivilServiceTest {

    @Mock
    public EstadoCivilRepository estadoCivilRepository;

    @InjectMocks
    public EstadoCivilService estadoCivilService;

    @Mock
    private Pageable pageableMock;

    @Test
    void getAll() {
        PageRequest paginator = PageRequest.of(1, 10);
        List<EstadoCivil> list = new ArrayList<>();
        list.add(createEstadoCivil());
        list.add(createEstadoCivil());
        list.add(createEstadoCivil());
        list.add(createEstadoCivil());
        Page<EstadoCivil> estadoCivilPage = new PageImpl<>(list, paginator, list.size());
        given(estadoCivilRepository.findAll(Mockito.any(Pageable.class))).willReturn(estadoCivilPage);

        List<EstadoCivil> expected = estadoCivilPage.getContent();
        List<EstadoCivil> result = estadoCivilService.getAll(pageableMock);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(expected.size());
        assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }

    private EstadoCivil createEstadoCivil() {
        return EstadoCivil.builder()
                .id(new Random().nextInt())
                .version(1)
                .nombre("Casado")
                .estado("A")
                .build();
    }

}