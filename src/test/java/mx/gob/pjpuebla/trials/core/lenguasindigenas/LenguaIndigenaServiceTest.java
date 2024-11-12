package mx.gob.pjpuebla.trials.core.lenguasindigenas;

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
class LenguaIndigenaServiceTest {

    @InjectMocks
    LenguaIndigenaService lenguaIndigenaService;
    @Mock
    LenguaIndigenaRepository lenguaIndigenaRepository;
    private LenguaIndigena lenguaIndigena;

    @BeforeEach
    public void setUp() {
        lenguaIndigena = new LenguaIndigena().setId(1).setKey(1).setName("Kaqchikel");
    }

    @Test
    void getAll() {
        List<LenguaIndigena> list = Collections.singletonList(lenguaIndigena);
        given(lenguaIndigenaRepository.findAllFilterByName(any())).willReturn(list);
        List<LenguaIndigenaRecord> results = lenguaIndigenaService.getAll("");
        assertThat(results)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", lenguaIndigena.getId())
                .hasFieldOrPropertyWithValue("name", StringUtils.capitalize(lenguaIndigena.getName().toLowerCase()));
    }
}
