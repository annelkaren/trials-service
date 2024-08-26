package mx.gob.pjpuebla.trials.core.organismos;

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
public class OrganismoServiceTest {


    @Mock
    public OrganismoRepository organismoRepository;

    @InjectMocks
    public  OrganismoService organismoService;

    @Mock
    private Pageable pageableMock;

    @DisplayName("Should return a response with a paginated list of Organismo items")
    @Test
    void getAll() {
        PageRequest paginator = PageRequest.of(1, 10);
        List<Organismo> list = new ArrayList<>();
        list.add(createOrganismo());
        list.add(createOrganismo());
        list.add(createOrganismo());
        list.add(createOrganismo());
        Page<Organismo> organismoPage = new PageImpl<>(list, paginator, list.size());
        given(organismoRepository.findAll(Mockito.any(Pageable.class))).willReturn(organismoPage);

        List<Organismo> expected = organismoPage.getContent();
        List<Organismo> result = organismoService.getAll(pageableMock);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(expected.size());
        assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }

    private Organismo createOrganismo(){
        return Organismo.builder()
                .id(new Random().nextInt())
                .version(1)
                .nombre("CONSEJO DE LA JUDICATURA DEL PODER JUDICIAL DEL ESTADO DE PUEBLA")
                .estado("A")
                .build();
    }
}