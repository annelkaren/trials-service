package mx.gob.pjpuebla.trials.core.nacionalidades;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NacionalidadServiceTest {

    @InjectMocks
    NacionalidadService nacionalidadService;
    @Mock
    NacionalidadRepository nacionalidadRepository;
    private Nacionalidad nacionalidad;

    @BeforeEach
    public void setUp() {
        nacionalidad = new Nacionalidad().setId(1).setKey(101).setName("NAMIBIANA").setAbbreviation("NAM");
    }

    @Test
    void getAll() {
        List<Nacionalidad> list = Collections.singletonList(nacionalidad);
        given(nacionalidadRepository.findAllFilterByName(any())).willReturn(list);
        List<NacionalidadRecord> results = nacionalidadService.getAll("");
        assertThat(results)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", nacionalidad.getId())
                .hasFieldOrPropertyWithValue("name", StringUtils.capitalize(nacionalidad.getName().toLowerCase()));
    }
}
