package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.personadetalle.PersonaDetalle;
import mx.gob.pjpuebla.trials.workflow.personadetalle.PersonaDetalleRepository;

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
public class PersonaDocumentoServiceTest {

    @Mock
    public PersonaDocumentoRepository personaDocumentoRepository;

    @Mock
    public PersonaDetalleRepository personaDetalleRepository;

    @Mock
    public DomicilioRepository domicilioRepository;

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

    @Test
    void getCorreoByPersonaDocumentoId_ShouldReturnCorreoWhenPersonaExists() {
        Integer id = 1;
        String expectedCorreo = "juan.perez@example.com";
        PersonaDocumento persona = new PersonaDocumento();
        persona.setId(id);
        persona.setCorreoElectronico(expectedCorreo);

        given(personaDocumentoRepository.findById(id)).willReturn(java.util.Optional.of(persona));

        String result = personaDocumentoService.getCorreoByPersonaDocumentoId(id);

        assertThat(result).isEqualTo(expectedCorreo);
    }

     @Test
    void getDomicilioByPersonaDocumentoId_ShouldReturnDomicilioWhenPersonaDetalleExists() {
        Integer documentoPersonaId = 1;
        Domicilio expectedDomicilio = new Domicilio();
        expectedDomicilio.setId(101L);
        
        PersonaDetalle personaDetalle = new PersonaDetalle();
        personaDetalle.setDomicilio(expectedDomicilio);

        given(personaDetalleRepository.findByPersonaDocumentoId(documentoPersonaId))
                .willReturn(java.util.Optional.of(personaDetalle));
        given(domicilioRepository.findById(expectedDomicilio.getId()))
                .willReturn(java.util.Optional.of(expectedDomicilio));

        Domicilio result = personaDocumentoService.getDomicilioByPersonaDocumentoId(documentoPersonaId);

        assertThat(result).isEqualTo(expectedDomicilio);
    }

}
