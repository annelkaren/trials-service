package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import mx.gob.pjpuebla.trials.core.organismos.Organismo;
import mx.gob.pjpuebla.trials.core.organismos.OrganismoRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PersonaDocumentoServiceTest {

    @Mock
    public PersonaDocumentoRepository personaDocumentoRepository;

    @InjectMocks
    public PersonaDocumentoService personaDocumentoService;

    @Test
    void getTipoPartesPrincipales() {
        Carpeta carpeta = new Carpeta().setId(51);
        PersonaDocumento entity = new PersonaDocumento().setId(1).setNombre("Juan").setApellidoPaterno("Perez").setCarpeta(carpeta);
        List<PersonaDocumento> listPage = Collections.singletonList(entity);

        given(personaDocumentoRepository.findByCarpetaIdAndRolAndTipoPartesNombre(any(), any(), any()))
                .willReturn(listPage);

        List<PersonaDocumentoNameRecord> resultList =  personaDocumentoService.getTipoPartesPrincipales(carpeta.getId(), "actor");
        assertThat(resultList)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", entity.getId())
                .hasFieldOrPropertyWithValue("nombreCompleto", entity.getNombre() + " " + entity.getApellidoPaterno());
    }

}
