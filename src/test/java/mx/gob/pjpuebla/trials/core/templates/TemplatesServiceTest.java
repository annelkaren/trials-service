package mx.gob.pjpuebla.trials.core.templates;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.templates.placeholders.Placeholders;
import mx.gob.pjpuebla.trials.core.templates.placeholders.PlaceholdersService;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TemplatesServiceTest {

    @Mock
    private TemplatesRepository templatesRepository;

    @Mock
    private PersonaService personaService;

    @Mock
    private PlaceholdersService placeholdersService;

    @InjectMocks
    private TemplatesService templatesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTempletesByJuzgado() {
        Persona auditor = new Persona();
        auditor.setJuzgado(JuzgadoSetUp.createJuzgado());
        when(personaService.getAuditor()).thenReturn(auditor);

        Templates template1 = TemplatesSetUp.createTemplates();
        Templates template2 = TemplatesSetUp.createTemplates();

        when(templatesRepository.findByJuzgadoId(1)).thenReturn(Arrays.asList(template1, template2));

        List<Templates> result = templatesService.getTempletesByJuzgado();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(templatesRepository, times(1)).findByJuzgadoId(1);
    }

    @Test
    void testGetTempletesAndPlaceholder() {
        Persona mockAuditor = new Persona();
        Juzgado mockJuzgado = new Juzgado();
        mockJuzgado.setId(1);
        mockAuditor.setJuzgado(mockJuzgado);

        when(personaService.getAuditor()).thenReturn(mockAuditor);

        Templates template = TemplatesSetUp.createTemplates();
        when(templatesRepository.findByJuzgadoId(1)).thenReturn(Arrays.asList(template));

        Placeholders placeholder = new Placeholders();
        when(placeholdersService.getPlaceholderList()).thenReturn(Arrays.asList(placeholder));

        List<TempletesPlaceholderRecord> result = templatesService.getTempletesAndPlaceholder();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(personaService, times(1)).getAuditor();
        verify(templatesRepository, times(1)).findByJuzgadoId(1);
        verify(placeholdersService, times(1)).getPlaceholderList();
    }


    @Test
    void testCreate() {
        Persona auditor = new Persona();
        auditor.setJuzgado(JuzgadoSetUp.createJuzgado());
        when(personaService.getAuditor()).thenReturn(auditor);

        Templates newTemplate = TemplatesSetUp.createTemplates();

        when(templatesRepository.save(newTemplate)).thenAnswer(invocation -> {
            Templates savedTemplate = invocation.getArgument(0);
            savedTemplate.setId(1);
            return savedTemplate;
        });

        TempleteRecordResponse response = templatesService.create(newTemplate);

        assertNotNull(response);
        assertEquals("Template 1", response.nombre());
        verify(templatesRepository, times(1)).save(newTemplate);
    }

    @Test
    void testUpdate() {
        Templates templateToUpdate = TemplatesSetUp.createTemplates();
        when(templatesRepository.save(templateToUpdate)).thenReturn(templateToUpdate);

        TempleteRecordResponse response = templatesService.update(templateToUpdate);

        assertNotNull(response);
        assertEquals("Template 1", response.nombre());
        verify(templatesRepository, times(1)).save(templateToUpdate);
    }

    @Test
    void testUpdateWithInvalidVersion() {
        Templates templateToUpdate = TemplatesSetUp.createTemplates();
        when(templatesRepository.save(templateToUpdate)).thenThrow(new org.springframework.dao.OptimisticLockingFailureException("Version conflict"));

        assertThrows(InvalidVersionException.class, () -> templatesService.update(templateToUpdate));
    }

    @Test
    void testDelete() {
        Integer idToDelete = 1;
        doNothing().when(templatesRepository).deleteById(idToDelete);
        templatesService.delete(idToDelete);

        verify(templatesRepository, times(1)).deleteById(idToDelete);
    }
}
