package mx.gob.pjpuebla.trials.core.oficialias;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRecordResponse;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialia;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRecord;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRepository;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaSetUp;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OficialiaServiceTest {

        @InjectMocks
        OficialiaService oficialiaService;

        @Mock
        OficialiaRepository oficialiaRepository;

        @Mock
        TipoOficialiaRepository tipoOficialiaRepository;

        @Mock
        SedeRepository sedeRepository;

        @Mock
        private JuzgadoRepository juzgadoRepository;
        @Mock
        private MateriaRepository materiaRepository;

        @Mock
        private Pageable pageableMock;

        private Oficialia oficialia;
        private OficialiaMateriaRecord oficialiaMateriaRecordResponse;
        private OficialiaRecord oficialiaRecord;
        private TipoOficialia tipoOficialia;
        private Sede sede;
        private List<Juzgado> juzgados;
        private Materia materia;

        @BeforeEach
        public void setUp() {
                tipoOficialia = TipoOficialiaSetUp.createtipoOficialia();
                Distrito distrito = DistritoSetUp.createDistrito();
                Domicilio domicilio = DomicilioSetUp.createDomicilio();
                juzgados = Arrays.asList(JuzgadoSetUp.createJuzgado(), JuzgadoSetUp.createJuzgado());
                sede = SedeSetUp.createSede();
                sede.setDistrito(distrito);
                sede.setDomicilio(domicilio);
                materia = MateriaSetUp.createMateria();

                oficialia = OficialiaSetUp.createOficialia(tipoOficialia, sede)
                                .setMaterias(List.of(materia));
                oficialia.setJuzgados(juzgados);
                oficialiaRecord = OficialiaSetUp.createOficialiaRecord(oficialia,
                                new TipoOficialiaRecord(tipoOficialia.getId(), tipoOficialia.getNombre()),
                                new SedeRecordResponse(sede.getId(), sede.getNombre(), sede.getEstado()));
        }

        @SuppressWarnings("unchecked")
        @Test
        void getAll_return_page() {
                List<Oficialia> listPage = Collections.singletonList(oficialia);
                given(oficialiaRepository.findAll(any(Example.class), any(PageRequest.class)))
                                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()),
                                                listPage.size()));
                Page<OficialiaRecord> page = oficialiaService.getAllActive(PageRequest.of(1, listPage.size()),
                                oficialia);
                assertThat(page.getContent())
                                .hasSize(1)
                                .first()
                                .hasFieldOrPropertyWithValue("id", oficialia.getId())
                                .hasFieldOrPropertyWithValue("version", oficialia.getVersion())
                                .hasFieldOrPropertyWithValue("estado", oficialia.getEstado())
                                .hasFieldOrPropertyWithValue("responsable", oficialia.getResponsable())
                                .hasFieldOrPropertyWithValue("nombre", oficialia.getNombre());

                assertThat(oficialia.getTipoOficialia())
                                .hasFieldOrPropertyWithValue("id", oficialia.getTipoOficialia().getId())
                                .hasFieldOrPropertyWithValue("nombre", oficialia.getTipoOficialia().getNombre());

                assertThat(oficialia.getSede())
                                .hasFieldOrPropertyWithValue("id", oficialia.getSede().getId())
                                .hasFieldOrPropertyWithValue("nombre", oficialia.getSede().getNombre())
                                .hasFieldOrPropertyWithValue("estado", oficialia.getSede().getEstado());
        }

        @Test
        void getById_return_oficialia() {
                List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
                given(oficialiaRepository.findByIdAndEstadoIn(oficialia.getId(), estados))
                                .willReturn(Optional.ofNullable(oficialiaRecord));

                OficialiaRecord mr = oficialiaService.findById(oficialia.getId());
                assertThat(mr).isOfAnyClassIn(OficialiaRecord.class)
                                .hasFieldOrPropertyWithValue("id", oficialia.getId())
                                .hasFieldOrPropertyWithValue("nombre", oficialia.getNombre());
        }

        @Test
        void getById_return_not_found() {
                Integer personaId = oficialia.getId();
                List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
                given(oficialiaRepository.findByIdAndEstadoIn(personaId, estados))
                                .willReturn(Optional.empty());

                NotFoundException assertThrows = assertThrows(
                                NotFoundException.class,
                                () -> oficialiaService.findById(personaId));

                assertThat(assertThrows.getMessage()).contains("Oficialia no encontrada");
        }

        @Test
        void create_throws_conflict_exception_when_oficialia_name_already_exists() {
                String existingName = "Existing Oficialia";
                Oficialia newOficialia = new Oficialia();
                newOficialia.setNombre(existingName);

                given(oficialiaRepository.findByNombreIgnoreCase(existingName))
                                .willReturn(Optional.of(new Oficialia()));

                ConflictException exception = assertThrows(
                                ConflictException.class,
                                () -> oficialiaService.create(newOficialia));

                assertThat(exception.getMessage()).contains("No pueden existir 2 oficialias con el mismo nombre");
        }

        @Test
        void create() {
                given(oficialiaRepository.findByNombreIgnoreCase(oficialia.getNombre()))
                                .willReturn(Optional.empty());
                given(sedeRepository.findById(oficialia.getSede().getId()))
                                .willReturn(Optional.ofNullable(oficialia.getSede()));
                given(tipoOficialiaRepository.findById(tipoOficialia.getId()))
                                .willReturn(Optional.of(tipoOficialia));
                given(juzgadoRepository.findAllById(anyList())).willReturn(juzgados);

                given(oficialiaRepository.save(oficialia))
                                .willReturn(oficialia);

                OficialiaRecordResponse response = oficialiaService.create(oficialia);

                assertThat(response).isOfAnyClassIn(OficialiaRecordResponse.class)
                                .hasFieldOrPropertyWithValue("id", oficialia.getId())
                                .hasFieldOrPropertyWithValue("nombre", oficialia.getNombre());
        }

        @Test
        void update() {
                given(oficialiaRepository.findById(oficialia.getId()))
                                .willReturn(Optional.ofNullable(oficialia));
                given(sedeRepository.findById(oficialia.getSede().getId()))
                                .willReturn(Optional.ofNullable(oficialia.getSede()));
                given(tipoOficialiaRepository.findById(tipoOficialia.getId()))
                                .willReturn(Optional.of(tipoOficialia));
                given(juzgadoRepository.findAllById(anyList())).willReturn(juzgados);
                given(oficialiaRepository.save(oficialia))
                                .willReturn(oficialia);

                OficialiaRecordResponse response = oficialiaService.update(oficialia);

                assertThat(response).isOfAnyClassIn(OficialiaRecordResponse.class)
                                .hasFieldOrPropertyWithValue("id", oficialia.getId())
                                .hasFieldOrPropertyWithValue("nombre", oficialia.getNombre());
        }

        @Test
        void update_return_optimistic_exception() {
                given(oficialiaRepository.findById(oficialia.getId()))
                                .willReturn(Optional.ofNullable(oficialia));
                given(sedeRepository.findById(oficialia.getSede().getId()))
                                .willReturn(Optional.ofNullable(oficialia.getSede()));
                given(tipoOficialiaRepository.findById(tipoOficialia.getId()))
                                .willReturn(Optional.of(tipoOficialia));
                given(oficialiaRepository.save(oficialia))
                                .willThrow(org.springframework.dao.OptimisticLockingFailureException.class);
                given(juzgadoRepository.findAllById(anyList())).willReturn(juzgados);

                InvalidVersionException assertThrows = assertThrows(
                                InvalidVersionException.class,
                                () -> oficialiaService.update(oficialia));

                assertThat(assertThrows.getMessage()).contains("Version modificada por otro usuario");
        }

        @Test
        void get_All_oficialia_materias() {
                oficialiaMateriaRecordResponse = new OficialiaMateriaRecord(
                                oficialia.getId(),
                                oficialia.getNombre(),
                                oficialia.getEstado(),
                                String.join(", ",
                                                oficialia.getMaterias().stream().map(Materia::getNombre)
                                                                .toArray(String[]::new)),
                                materia.getId(),
                                sede.getId(),
                                tipoOficialia.getNombre(),
                                tipoOficialia.getId(),
                                juzgados.get(0).getNombre(),
                                juzgados.get(0).getId());

                Oficialia tmp = OficialiaSetUp.createOficialia(tipoOficialia, sede);
                tmp.setMaterias(List.of(materia));
                tmp.setJuzgados(List.of(juzgados.get(0)));
                tmp.setTipoOficialia(tipoOficialia);

                List<OficialiaMateriaRecord> listPage = Collections.singletonList(oficialiaMateriaRecordResponse);
                List<Oficialia> list = Collections.singletonList(tmp);

                given(oficialiaRepository.findAllActive(any(), any(PageRequest.class)))
                                .willReturn(new PageImpl<>(list, PageRequest.of(0, list.size()), list.size()));

                Page<OficialiaMateriaRecord> page = oficialiaService.getAllByOficialiaMateria(null,
                                PageRequest.of(1, listPage.size()));

                assertThat(page.getContent())
                                .hasSize(1)
                                .first()
                                .hasFieldOrPropertyWithValue("id", oficialia.getId())
                                .hasFieldOrPropertyWithValue("nombre", oficialia.getNombre())
                                .hasFieldOrPropertyWithValue("estado", oficialia.getEstado());

                for (Materia materiaItem : oficialia.getMaterias()) {
                        assertThat(materiaItem)
                                        .hasFieldOrPropertyWithValue("id", materiaItem.getId())
                                        .hasFieldOrPropertyWithValue("nombre", materiaItem.getNombre());
                }

                assertThat(oficialia.getSede())
                                .hasFieldOrPropertyWithValue("id", oficialia.getSede().getId())
                                .hasFieldOrPropertyWithValue("nombre", oficialia.getSede().getNombre());

                assertThat(oficialia.getTipoOficialia())
                                .hasFieldOrPropertyWithValue("id", oficialia.getTipoOficialia().getId())
                                .hasFieldOrPropertyWithValue("nombre", oficialia.getTipoOficialia().getNombre());

                for (Juzgado juzgadoItem : oficialia.getJuzgados()) {
                        assertThat(juzgadoItem)
                                        .hasFieldOrPropertyWithValue("id", juzgadoItem.getId())
                                        .hasFieldOrPropertyWithValue("nombre", juzgadoItem.getNombre());
                }
        }

        @Test
        void validateJuzgadosByTipoOficialia_comun_withTwoJuzgados_shouldNotThrow() {
                TipoOficialia tipoComun = new TipoOficialia().setNombre("Común");
                Oficialia oficialia = new Oficialia().setJuzgados(Arrays.asList(new Juzgado(), new Juzgado()));

                assertDoesNotThrow(() -> oficialiaService.validateJuzgadosByTipoOficialia(oficialia, tipoComun));
        }

        @Test
        void validateJuzgadosByTipoOficialia_comun_withLessThanTwoJuzgados_shouldThrow() {
                TipoOficialia tipoComun = new TipoOficialia().setNombre("Común");
                Oficialia oficialia = new Oficialia().setJuzgados(Collections.singletonList(new Juzgado()));

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                () -> oficialiaService.validateJuzgadosByTipoOficialia(oficialia, tipoComun));
                assertThat(exception.getMessage())
                                .isEqualTo("Las oficialías de tipo Común deben tener al menos dos juzgados.");
        }

        @Test
        void validateJuzgadosByTipoOficialia_mayor_withOneJuzgado_shouldNotThrow() {
                TipoOficialia tipoMayor = new TipoOficialia().setNombre("Mayor");
                Oficialia oficialia = new Oficialia().setJuzgados(Collections.singletonList(new Juzgado()));

                assertDoesNotThrow(() -> oficialiaService.validateJuzgadosByTipoOficialia(oficialia, tipoMayor));
        }

        @Test
        void validateJuzgadosByTipoOficialia_mayor_withMoreThanOneJuzgado_shouldThrow() {
                TipoOficialia tipoMayor = new TipoOficialia().setNombre("Mayor");
                Oficialia oficialia = new Oficialia().setJuzgados(Arrays.asList(new Juzgado(), new Juzgado()));

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                () -> oficialiaService.validateJuzgadosByTipoOficialia(oficialia, tipoMayor));
                assertThat(exception.getMessage())
                                .isEqualTo("Las oficialías de tipo Mayor deben tener exactamente un juzgado.");
        }

        @Test
        void validateJuzgadosByTipoOficialia_mayor_withNoJuzgados_shouldThrow() {
                TipoOficialia tipoMayor = new TipoOficialia().setNombre("Mayor");
                Oficialia oficialia = new Oficialia().setJuzgados(Collections.emptyList());

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                () -> oficialiaService.validateJuzgadosByTipoOficialia(oficialia, tipoMayor));
                assertThat(exception.getMessage())
                                .isEqualTo("Las oficialías de tipo Mayor deben tener exactamente un juzgado.");
        }
}
