package mx.gob.pjpuebla.trials.core.templates;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.gob.pjpuebla.trials.core.templates.placeholders.Placeholders;
import mx.gob.pjpuebla.trials.core.templates.placeholders.PlaceholdersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TemplatesResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
class TemplatesResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TemplatesService templatesService;

    @MockitoBean
    private PlaceholdersService placeholdersService;

    @Test
    void getAllPlaceholders() throws Exception {
        Placeholders placeholder1 = new Placeholders();
        placeholder1.setId(1);
        placeholder1.setNombre("Placeholder 1");

        Placeholders placeholder2 = new Placeholders();
        placeholder2.setId(2);
        placeholder2.setNombre("Placeholder 2");

        when(placeholdersService.getPlaceholderList()).thenReturn(List.of(placeholder1, placeholder2));

        mockMvc.perform(get("/api/core/templates/placeholders"))
                .andExpect(status().isOk());
        verify(placeholdersService, times(1)).getPlaceholderList();
    }

    @Test
    void getTemplatesAndPlaceholders() throws Exception {
        TempletesPlaceholderRecord record1 = new TempletesPlaceholderRecord("Template 1", "<P>HOLA 1</P>", false);
        TempletesPlaceholderRecord record2 = new TempletesPlaceholderRecord("Template 2", "<P>HOLA 2</P>", false);

        when(templatesService.getTempletesAndPlaceholder()).thenReturn(List.of(record1, record2));

        mockMvc.perform(get("/api/core/templates/"))
                .andExpect(status().isOk());
        verify(templatesService, times(1)).getTempletesAndPlaceholder();
    }

    @Test
    void create() throws Exception {
        Templates template = TemplatesSetUp.createTemplates();
        TempleteRecordResponse response = new TempleteRecordResponse("Template 1");

        when(templatesService.create(Mockito.any(Templates.class))).thenReturn(response);

        mockMvc.perform(post("/api/core/templates/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(template)))
                .andExpect(status().isOk());

        verify(templatesService, times(1)).create(Mockito.any(Templates.class));
    }

    @Test
    void update() throws Exception {
        Templates template = TemplatesSetUp.createTemplates();

        TempleteRecordResponse response = new TempleteRecordResponse("Template Updated");

        when(templatesService.update(Mockito.any(Templates.class))).thenReturn(response);

        mockMvc.perform(put("/api/core/templates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(template)))
                .andExpect(status().isOk());
        verify(templatesService, times(1)).update(Mockito.any(Templates.class));
    }

    @Test
    void delete_template() throws Exception {
        doNothing().when(templatesService).delete(1);

        mockMvc.perform(delete("/api/core/templates/1"))
                .andExpect(status().isOk());

        verify(templatesService, times(1)).delete(1);
    }

}
