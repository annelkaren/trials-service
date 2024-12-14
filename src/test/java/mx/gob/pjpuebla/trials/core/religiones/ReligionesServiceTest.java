package mx.gob.pjpuebla.trials.core.religiones;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReligionesServiceTest {

    @InjectMocks
    ReligionesService religionesService;

    @Mock
    ReligionesRepository religionesRepository;
    private ReligionesRecord religionesRecord;

    @BeforeEach
    void setUp() {
        religionesRecord = new ReligionesRecord(1, "Católica");
    }

    @Test
    @Transactional(readOnly = true)
    void testFindAllByReligionesAutocomplete() {
        when(religionesRepository.findAllByidReligion("Católica")).thenReturn(Collections.singletonList(religionesRecord));

        List<ReligionesRecord> result = religionesService.findAllByReligionesAutocomplete("Católica");

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).idReligion());
        assertEquals("Católica", result.get(0).nombreReligion());
    }

}