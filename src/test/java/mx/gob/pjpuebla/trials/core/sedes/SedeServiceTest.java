package mx.gob.pjpuebla.trials.core.sedes;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SedeServiceTest {

    @Mock
    SedeRepository mockSedeRepository;

    @Mock
    DistritoRepository mockDistritoRepository;

    @Mock
    DomicilioRepository mockDomicilioRepository;

    @InjectMocks
    SedeService sedeService;

    @Mock
    DomicilioService domicilioService;

    private Sede sede;
    private Sede sedeDomicilio;
    private SedeRecord sedeRecord;
    private Distrito distrito;
    private Domicilio domicilio;
    private SedeDomiciliosRecord sedeDomiciliosRecord;

    @BeforeEach
    public void setUp() {
        sede = SedeSetUp.createSede(Estado.ACTIVE);
        sedeRecord = SedeSetUp.sedeRecord();
        distrito = DistritoSetUp.createDistrito();
        domicilio = DomicilioSetUp.createDomicilio();
        sedeDomicilio = SedeSetUp.createSede(domicilio);
    }

    @Test
    void getAll_return_page() {
        List<Sede> listPage = Collections.singletonList(sedeDomicilio);
        given(mockSedeRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<SedeDomicilioRecordResponse> page = sedeService.getAll(sedeDomicilio, PageRequest.of(1, listPage.size()));
        assertThat(page.getContent())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", sedeDomicilio.getId())
                .hasFieldOrPropertyWithValue("nombre", sedeDomicilio.getNombre())
                .hasFieldOrPropertyWithValue("estado", sedeDomicilio.getEstado());

        assertThat(sedeDomicilio.getDomicilio())
                .hasFieldOrPropertyWithValue("id", sedeDomicilio.getDomicilio().getId())
                .hasFieldOrPropertyWithValue("calle", sedeDomicilio.getDomicilio().getCalle())
                .hasFieldOrPropertyWithValue("exterior", sedeDomicilio.getDomicilio().getExterior())
                .hasFieldOrPropertyWithValue("interior", sedeDomicilio.getDomicilio().getInterior())
                .hasFieldOrPropertyWithValue("estadoRepublica", sedeDomicilio.getDomicilio().getEstadoRepublica())
                .hasFieldOrPropertyWithValue("municipio", sedeDomicilio.getDomicilio().getMunicipio())
                .hasFieldOrPropertyWithValue("localidad", sedeDomicilio.getDomicilio().getLocalidad())
                .hasFieldOrPropertyWithValue("colonia", sedeDomicilio.getDomicilio().getColonia())
                .hasFieldOrPropertyWithValue("codigoPostal", sedeDomicilio.getDomicilio().getCodigoPostal())
                .hasFieldOrPropertyWithValue("referencia", sedeDomicilio.getDomicilio().getReferencia());
    }

    @Test
    void getById_return_sedeRecord() {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(mockSedeRepository.findByIdAndEstadoIn(sede.getId(), estados))
                .willReturn(Optional.ofNullable(sedeRecord));

        SedeRecord result = sedeService.findById(sede.getId());
        assertThat(result).isOfAnyClassIn(SedeRecord.class)
                .hasFieldOrPropertyWithValue("id", sede.getId())
                .hasFieldOrPropertyWithValue("nombre", sede.getNombre());
    }

    @Test
    void getById_return_not_found() {
        Integer id = sede.getId();
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(mockSedeRepository.findByIdAndEstadoIn(sede.getId(), estados))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> sedeService.findById(id)
        );

        assertThat(assertThrows.getMessage()).contains("Sede no encontrada");
    }

    @Test
    void create_throws_conflict_exception_when_nombre_already_exists() {
        given(mockSedeRepository.findByNombre(sede.getNombre())).willReturn(Optional.of(sede));
        ConflictException assertThrows = assertThrows(
        ConflictException.class,
                () -> sedeService.create(sede)
        );

        assertThat(assertThrows.getMessage()).contains("No pueden existir 2 sedes con el mismo nombre");
    }

    @Test
    void create() {
        given(mockSedeRepository.findByNombre(sede.getNombre()))
                .willReturn(Optional.empty());
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        given(mockDistritoRepository.findById(distrito.getId()))
                .willReturn(Optional.ofNullable(distrito));
        given(domicilioService.save(domicilio))
                .willReturn(domicilio);
        given(mockSedeRepository.save(sede))
                .willReturn(sede);

        SedeRecordResponse response = sedeService.create(sede);

        assertThat(response).isOfAnyClassIn(SedeRecordResponse.class)
                .hasFieldOrPropertyWithValue("id", sede.getId())
                .hasFieldOrPropertyWithValue("nombre", sede.getNombre())
                .hasFieldOrPropertyWithValue("estado", sede.getEstado());
    }

    @Test
    void update() {
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        
        given(mockDistritoRepository.findById(distrito.getId()))
                .willReturn(Optional.ofNullable(distrito));
        given(domicilioService.save(domicilio))
                .willReturn(domicilio);
        given(mockSedeRepository.save(sede))
                .willReturn(sede);

        SedeRecordResponse response = sedeService.update(sede);

        assertThat(response).isOfAnyClassIn(SedeRecordResponse.class)
                .hasFieldOrPropertyWithValue("id", sede.getId())
                .hasFieldOrPropertyWithValue("nombre", sede.getNombre())
                .hasFieldOrPropertyWithValue("estado", sede.getEstado());
    }

    @Test
    void update_return_optimistic_exception() {
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        sede.setVersion(8);
        given(mockDistritoRepository.findById(distrito.getId()))
                .willReturn(Optional.ofNullable(distrito));
        given(domicilioService.save(domicilio))
                .willReturn(domicilio);
        given(mockSedeRepository.save(sede)).willThrow(org.springframework.dao.OptimisticLockingFailureException.class);

        InvalidVersionException assertThrows = assertThrows(
                InvalidVersionException.class,
                () -> sedeService.update(sede)
        );

        assertThat(assertThrows.getMessage()).contains("Version modificada por otro usuario");
    }
    @Test
    void getAllSedesAndDomicilios_return_page() {
        // Inicializar el objeto SedeDomiciliosRecord
        sedeDomiciliosRecord = new SedeDomiciliosRecord(
                sede.getId(),
                sede.getNombre(),
                domicilio.getCalle(),
                domicilio.getInterior(),
                domicilio.getExterior(),
                domicilio.getColonia(),
                domicilio.getCodigoPostal(),
                domicilio.getMunicipio(),
                domicilio.getEstadoRepublica(),
                domicilio.getReferencia(),
                domicilio.getLocalidad()
        );

        List<SedeDomiciliosRecord> listPage = Collections.singletonList(sedeDomiciliosRecord);
        given(mockSedeRepository.findSedesDomiciliosByJuzgadoId(any(Pageable.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));

        // Llamar al método de servicio
        Page<SedeDomiciliosRecord> page = sedeService.getAllSedesAndDomicilios(PageRequest.of(0, 10));

        assertThat(page.getContent())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("sedeId", sede.getId())
                .hasFieldOrPropertyWithValue("sedeNombre", sede.getNombre())
                .hasFieldOrPropertyWithValue("calle", domicilio.getCalle())
                .hasFieldOrPropertyWithValue("interior", domicilio.getInterior())
                .hasFieldOrPropertyWithValue("exterior", domicilio.getExterior())
                .hasFieldOrPropertyWithValue("colonia", domicilio.getColonia())
                .hasFieldOrPropertyWithValue("codigoPostal", domicilio.getCodigoPostal())
                .hasFieldOrPropertyWithValue("municipio", domicilio.getMunicipio())
                .hasFieldOrPropertyWithValue("estadoRepublica", domicilio.getEstadoRepublica())
                .hasFieldOrPropertyWithValue("referencia", domicilio.getReferencia())
                .hasFieldOrPropertyWithValue("localidad", domicilio.getLocalidad());
    }

}
