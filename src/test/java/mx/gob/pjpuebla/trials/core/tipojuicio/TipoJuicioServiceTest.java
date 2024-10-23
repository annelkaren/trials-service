package mx.gob.pjpuebla.trials.core.tipojuicio;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.oficialias.OficialiaSetUp;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialia;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaSetUp;
import mx.gob.pjpuebla.trials.error.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static mx.gob.pjpuebla.trials.core.materias.MateriaSetUp.createMateria;
import static mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp.createTipoJuicio;
import static mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp.createTipoSistema;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoJuicioServiceTest {

    @Mock
    TipoJuicioRepository mockTipoJuicioRepository;

    @Mock
    PersonaService personaService;

    @InjectMocks
    TipoJuicioService target;

    private TipoJuicio validTipoJuicio;

    @BeforeEach
    public void setUp() {
        validTipoJuicio = createTipoJuicio(createTipoSistema(), createMateria());
    }

    @Test
    void getAll_return_page() {
        List<TipoJuicio> listPage = Collections.singletonList(validTipoJuicio);
        given(mockTipoJuicioRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<TipoJuicioRecord> page = target.getAllActive(PageRequest.of(1, listPage.size()), validTipoJuicio);
        assertThat(page.getContent())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", validTipoJuicio.getId())
                .hasFieldOrPropertyWithValue("nombre", validTipoJuicio.getNombre());
    }

    @Test
    void getById_return_tipojuicio() {
        given(mockTipoJuicioRepository.findByIdAndEstado(validTipoJuicio.getId(), validTipoJuicio.getEstado()))
                .willReturn(Optional.ofNullable(validTipoJuicio));

        TipoJuicioRecord tjr = target.findById(validTipoJuicio.getId());
        assertThat(tjr).isOfAnyClassIn(TipoJuicioRecord.class)
                .hasFieldOrPropertyWithValue("id", validTipoJuicio.getId())
                .hasFieldOrPropertyWithValue("nombre", validTipoJuicio.getNombre());
    }

    @Test
    void getById_return_not_found() {
        given(mockTipoJuicioRepository.findByIdAndEstado(validTipoJuicio.getId(), validTipoJuicio.getEstado()))
                .willReturn(Optional.empty());
        Integer id = validTipoJuicio.getId();
        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    target.findById(id);
                });

        assertThat(assertThrows.getMessage()).contains("Tipo de Juicio no encontrado");

    }

    @Test
    void getAllTipoJuicios_success() {

        TipoJuicioDemandasRecord record = new TipoJuicioDemandasRecord(1, "Familiar Oralidad (Alimentos)");
        List<TipoJuicioDemandasRecord> expectedResults = List.of(record);

        when(mockTipoJuicioRepository.findByAllTipoJuicios("Oral", "FAMILIAR"))
                .thenReturn(expectedResults);

        List<TipoJuicioDemandasRecord> results = target.getAllTipoJuicios();
        assertEquals(expectedResults, results);
    }

    @Test
    void getAllTipoJuicios_notFound() {
        // Arrange
        when(mockTipoJuicioRepository.findByAllTipoJuicios("Oral", "FAMILIAR"))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            target.getAllTipoJuicios();
        });

       
        assertEquals("No se encontraron tipos de juicio para Oral y FAMILIAR", exception.getReason());
    }

    @Test
    void getAllTipoJuiciosByOficialia(){
        List<TipoJuicio> tipoJuicios = List.of(validTipoJuicio);
        Page<TipoJuicio> page = new PageImpl<>(tipoJuicios);

        Persona persona = PersonaSetUp.createPersona();
        TipoOficialia tipoOficialia = TipoOficialiaSetUp.createtipoOficialia();
        Sede sede = SedeSetUp.createSede();
        Juzgado juzgado = JuzgadoSetUp.createJuzgado(validTipoJuicio.getMateria(), sede);
        juzgado.setTipoJuicios(tipoJuicios);

        Oficialia oficialia = OficialiaSetUp.createOficialia(tipoOficialia, sede);
        oficialia.setJuzgados(List.of(juzgado));

        persona.setOficialia(oficialia);

        given(personaService.getAuditor()).willReturn(persona);
        given(mockTipoJuicioRepository.findByOficialia(any(), any())).willReturn(page);

        Page<TipoJuicioRecord> results = target.getAllActiveByOficialia(PageRequest.of(0, 20));

        assertThat(results).isNotEmpty().anyMatch(p -> p.nombre().equals(validTipoJuicio.getNombre()));
    }

}