package mx.gob.pjpuebla.trials.core.templates.placeholders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PlaceholdersServiceTest {

    @Mock
    private PlaceholdersRepository placeholdersRepository;

    @InjectMocks
    private PlaceholdersService placeholdersService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPlaceholderList() {
        // Mock data
        Placeholders placeholder1 = new Placeholders();
        Placeholders placeholder2 = new Placeholders();
        placeholder1.setId(1);
        placeholder1.setNombre("name");
        placeholder1.setContenido("<p>hola</p>");
        placeholder2.setId(2);
        placeholder2.setNombre("name");
        placeholder2.setContenido("<p>hola</p>");


        when(placeholdersRepository.findAll()).thenReturn(Arrays.asList(placeholder1, placeholder2));

        List<Placeholders> result = placeholdersService.getPlaceholderList();

        assertEquals(2, result.size());
        assertEquals("name", result.get(0).getNombre());
        assertEquals("name", result.get(1).getNombre());
    }
}