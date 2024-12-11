package mx.gob.pjpuebla.trials.core.instituciones;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.error.NotFoundException;

import mx.gob.pjpuebla.trials.util.enums.Estado;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class InstitucionServiceTest {

    @Mock
    InstitucionRepository mockInstitucionRepository;

    @Mock
    DistritoRepository mockDistritoRepository;

    @Mock
    DomicilioRepository mockDomicilioRepository;

    @InjectMocks
    InstitucionService mockInstitucionService;

    private Institucion institucion;
    private InstitucionRecordResponse institucionRecordResponse;
    private Domicilio domicilio;

    @BeforeEach
    public void setUp() {
        institucion = InstitucionSetUp.createInstitucion(Estado.ACTIVE);
        institucionRecordResponse = InstitucionSetUp.createInstitucionRecordResponse();

        domicilio = DomicilioSetUp.createDomicilio();
    }


    @Test
    void getAll_return_page() {
        institucion.setDomicilio(domicilio);

        List<Institucion> listPage = Collections.singletonList(institucion);
        given(mockInstitucionRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()),
                        listPage.size()));
        Page<InstitucionRecord> page = mockInstitucionService.getAll(institucion,
                PageRequest.of(1, listPage.size()));
        assertThat(page.getContent())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", institucion.getId())
                .hasFieldOrPropertyWithValue("nombre", institucion.getNombre());

    }

    @Test
    void getById_return_institucionRecordResponse() {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(mockInstitucionRepository.findByIdAndEstadoIn(institucion.getId(), estados))
                .willReturn(Optional.ofNullable(institucionRecordResponse));

        InstitucionRecordResponse result = mockInstitucionService.findById(institucion.getId());
        assertThat(result).isOfAnyClassIn(InstitucionRecordResponse.class)
                .hasFieldOrPropertyWithValue("id", institucion.getId())
                .hasFieldOrPropertyWithValue("nombre", institucion.getNombre());
    }

    @Test
    void getById_return_not_found() {
        Integer id = institucion.getId();
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(mockInstitucionRepository.findByIdAndEstadoIn(institucion.getId(), estados))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> mockInstitucionService.findById(id));

        assertThat(assertThrows.getMessage()).contains("Institución no encontrada");
    }

    @Test
    void create_throws_conflict_exception_when_nombre_already_exists() {
        given(mockInstitucionRepository.findByNombre(institucion.getNombre())).willReturn(Optional.of(institucion));
        ConflictException thrown = assertThrows(
            ConflictException.class,
            () -> mockInstitucionService.create(institucion));
    
        assertThat(thrown.getMessage()).contains("No pueden existir 2 instituciones con el mismo nombre");
    }
    

    @Test
    void create() {
        given(mockInstitucionRepository.findByNombre(institucion.getNombre()))
                .willReturn(Optional.empty());
        institucion.setDomicilio(domicilio);

        given(mockDomicilioRepository.save(domicilio))
                .willReturn(domicilio);
        given(mockInstitucionRepository.save(institucion))
                .willReturn(institucion);

        Integer response = mockInstitucionService.create(institucion);

        assertThat(response).isEqualTo(institucion.getId());
    }

    @Test
    void update_success() {

        institucion.setDomicilio(domicilio);

        given(mockDomicilioRepository.save(domicilio))
                .willReturn(domicilio);

        given(mockInstitucionRepository.save(institucion))
                .willReturn(institucion);

        Integer response = mockInstitucionService.update(institucion);

        assertThat(response).isEqualTo(institucion.getId());
    }

    @Test
    void update_return_optimistic_exception() {
        institucion.setDomicilio(domicilio);
        institucion.setVersion(8);

        given(mockDomicilioRepository.save(domicilio))
                .willReturn(domicilio);

        given(mockInstitucionRepository.save(institucion))
                .willThrow(org.springframework.dao.OptimisticLockingFailureException.class);

        InvalidVersionException assertThrows = assertThrows(
                InvalidVersionException.class,
                () -> mockInstitucionService.update(institucion));

        assertThat(assertThrows.getMessage()).contains("Version modificada por otro usuario");

    }

    @Test
    void delete_success() {

        doNothing().when(mockInstitucionRepository).deleteById(anyInt());

        mockInstitucionService.delete(1);

        verify(mockInstitucionRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_nonExistentId_throwsException() {

        doThrow(new IllegalArgumentException("Invalid ID")).when(mockInstitucionRepository).deleteById(anyInt());


        assertThatThrownBy(() -> mockInstitucionService.delete(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");
    }

    @Test
    void getAllByEstadoAutocomplete_return_page() {
        institucion.setDomicilio(domicilio);
        institucion.setEstado(Estado.ACTIVE);

        List<Institucion> listPage = Collections.singletonList(institucion);

        given(mockInstitucionRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<InstitucionRecord> page = mockInstitucionService.getAllByEstadoAutocomplete(institucion, PageRequest.of(0, listPage.size()));
        assertThat(page.getContent())
                .hasSize(1);
        assertThat(page.getContent().get(0))
                .extracting("id", "nombre", "domicilio", "telefono")
                .containsExactly(
                        institucion.getId(),
                        institucion.getNombre(),
                        String.join(" ",
                                domicilio.getCalle(),
                                domicilio.getColonia(),
                                domicilio.getExterior(),
                                (domicilio.getInterior() != null && !domicilio.getInterior().isEmpty()) ? "Int. " + domicilio.getInterior() : "",
                                domicilio.getEstadoRepublica(),
                                domicilio.getMunicipio(),
                                domicilio.getLocalidad(),
                                domicilio.getCodigoPostal(),
                                (domicilio.getReferencia() != null && !domicilio.getReferencia().isEmpty()) ? "Ref: " + domicilio.getReferencia() : ""
                        ).trim(),
                        institucion.getTelefono()
                );
    }

}
