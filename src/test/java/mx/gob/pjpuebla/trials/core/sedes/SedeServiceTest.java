package mx.gob.pjpuebla.trials.core.sedes;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioService;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeDomicilioRecordResponse;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeDomiciliosRecord;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeRecord;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeRecordResponse;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.documentos.DigitalizacionService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SedeServiceTest {

        @Mock
        SedeRepository mockSedeRepository;

        @Mock
        DistritoRepository mockDistritoRepository;

        @Mock
        DomicilioRepository mockDomicilioRepository;

        @Mock
        DigitalizacionService mockDigitalizacionService;

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
                Sede example = sede;
                Pageable pageable = PageRequest.of(0, 10);
                Long idDoimicilioRecord = (long) 1;
                DomicilioRecord domRecord = new DomicilioRecord(idDoimicilioRecord, "calle", "exterior", "interior",
                                "estadoRepublica", "municipio", "localidad", "Colonia", "Codigo postal", "Referencia",
                                "Ciudad");

                SedeDomicilioRecordResponse record = new SedeDomicilioRecordResponse(
                                1,
                                "Sede",
                                Estado.ACTIVE,
                                domRecord,
                                "Telefono");

                Page<SedeDomicilioRecordResponse> mockedPage = new PageImpl<>(
                                List.of(record),
                                pageable,
                                1);

                given(mockSedeRepository.findAllSedeDomicilioWithPagination(
                                anyString(),
                                anyString(),
                                anyString(),
                                any(List.class), 
                                any(Pageable.class))).willReturn(mockedPage);

                // Act
                Page<SedeDomicilioRecordResponse> result = sedeService.getAll( anyString(), anyString(), anyString(), anyString(), any(Estado.class), pageable);

                // Assert
                assertThat(result).isNotNull();
                assertThat(result.getContent()).hasSize(1);
                assertThat(result.getContent().get(0).nombre()).isEqualTo("Sede");
        }

        @Test
        void getById_return_sedeRecord() {
                List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
                given(mockSedeRepository.findByIdAndEstadoIn(sede.getId(), estados))
                                .willReturn(Optional.ofNullable(sedeRecord));
                given(mockDigitalizacionService.getPhoto(any(), any())).willReturn("data:image/png;base64, iVBOR");
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
                                () -> sedeService.findById(id));

                assertThat(assertThrows.getMessage()).contains("Sede no encontrada");
        }

        @Test
        void create_throws_conflict_exception_when_nombre_already_exists() {
                given(mockSedeRepository.findByNombre(sede.getNombre())).willReturn(Optional.of(sede));
                ConflictException assertThrows = assertThrows(
                                ConflictException.class,
                                () -> sedeService.create(sede, null));

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

                SedeRecordResponse response = sedeService.create(sede, null);

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

                SedeRecordResponse response = sedeService.update(sede, null);

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
                given(mockSedeRepository.save(sede))
                                .willThrow(org.springframework.dao.OptimisticLockingFailureException.class);

                InvalidVersionException assertThrows = assertThrows(
                                InvalidVersionException.class,
                                () -> sedeService.update(sede, null));

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
                                domicilio.getLocalidad());

                List<SedeDomiciliosRecord> listPage = Collections.singletonList(sedeDomiciliosRecord);
                given(mockSedeRepository.findSedesDomiciliosByJuzgadoId(any(Pageable.class)))
                                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()),
                                                listPage.size()));

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
