package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.error.ConstraintViolationException;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.Messages;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.InstanciaJuzgado;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFolios;
import mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFoliosRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.*;

import static mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp.createTipoJuicio;
import static mx.gob.pjpuebla.trials.util.Messages.OPTIMISTIC_LOCKING_ERROR;
import static mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFoliosSetUp.createJuzgadoFolios;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class JuzgadoServiceTest {

    @InjectMocks
    JuzgadoService juzgadoService;
    @Mock
    JuzgadoRepository juzgadoRepository;
    @Mock
    SedeRepository sedeRepository;
    @Mock
    TipoJuicioRepository tipoJuicioRepository;
    @Mock
    MateriaRepository materiaRepository;
    @Mock
    JuzgadoFoliosRepository juzgadoFoliosRepository;
    @Mock
    PersonaService personaService;

    private Juzgado juzgado;
    private JuzgadoFolios juzgadoFolios;
    private JuzgadoRecordItem juzgadoRecordItem;

    @BeforeEach
    public void setUp() {
        Materia materia = MateriaSetUp.createMateria();
        Distrito distrito = DistritoSetUp.createDistrito();
        Domicilio domicilio = DomicilioSetUp.createDomicilio();
        Sede sede = SedeSetUp.createSede();
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        TipoSistema tipoSistema = TipoSistemaSetUp.createTipoSistema();
        TipoJuicio tipoJuicio = createTipoJuicio(tipoSistema, materia);
        juzgado = JuzgadoSetUp.createJuzgado(materia, sede)
                .setTipoJuicios(List.of(tipoJuicio));
        juzgadoFolios = createJuzgadoFolios();
        juzgadoFolios.setJuzgado(juzgado);
        juzgadoRecordItem = JuzgadoSetUp.createJuzgadoRecordResponse(juzgado, materia.getNombre());
    }

    @Test
    void getAll_return_page() {
        List<Juzgado> listPage = Collections.singletonList(juzgado);
        given(juzgadoRepository.findAll(any(), any(), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<JuzgadoRecordItem> page = juzgadoService.getAll("", PageRequest.of(1, listPage.size()));
        assertThat(page.getContent())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", juzgado.getId())
                .hasFieldOrPropertyWithValue("nombre", juzgado.getNombre())
                .hasFieldOrPropertyWithValue("estado", juzgado.getEstado())
                .hasFieldOrPropertyWithValue("materia", StringUtils.capitalize(juzgado.getMateria().getNombre().toLowerCase()));
    }

    @Test
    void getById_return_juzgadoRecord() {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(juzgadoRepository.findByIdAndEstadoIn(juzgado.getId(), estados))
                .willReturn(Optional.ofNullable(juzgado));

        JuzgadoRecord result = juzgadoService.findById(juzgado.getId());
        assertThat(result).isOfAnyClassIn(JuzgadoRecord.class)
                .hasFieldOrPropertyWithValue("id", juzgado.getId())
                .hasFieldOrPropertyWithValue("nombre", juzgado.getNombre())
                .hasFieldOrProperty("tipoJuicios");
        assertThat(result.tipoJuicios())
                .hasSize(1);
    }

    @Test
    void getById_return_not_found() {
        Integer id = juzgado.getId();
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(juzgadoRepository.findByIdAndEstadoIn(juzgado.getId(), estados))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> juzgadoService.findById(id)
        );

        assertThat(assertThrows.getMessage()).contains("Juzgado no encontrado");
    }

    @Test
    void create_throwsConflictException_whenJuzgadoWithSameNameExists() {
        given(juzgadoRepository.findByNombreIgnoreCase(juzgado.getNombre()))
                .willReturn(Optional.of(juzgado));

        assertThrows(ConflictException.class, () -> {
                juzgadoService.create(juzgado);
        });

        verify(juzgadoRepository, never()).save(any(Juzgado.class));
    }

    @Test
    void create() {
        given(juzgadoRepository.findByNombreIgnoreCase(juzgado.getNombre()))
        .willReturn(Optional.empty());

        given(materiaRepository.findById(juzgado.getMateria().getId()))
                .willReturn(Optional.ofNullable(juzgado.getMateria()));
        given(sedeRepository.findById(juzgado.getSede().getId()))
                .willReturn(Optional.ofNullable(juzgado.getSede()));
        given(juzgadoRepository.save(juzgado))
                .willReturn(juzgado);

        JuzgadoRecordItem response = juzgadoService.create(juzgado);

        assertThat(response).isOfAnyClassIn(JuzgadoRecordItem.class)
                .hasFieldOrPropertyWithValue("id", juzgado.getId())
                .hasFieldOrPropertyWithValue("nombre", juzgado.getNombre())
                .hasFieldOrPropertyWithValue("estado", juzgado.getEstado())
                .hasFieldOrPropertyWithValue("materia", juzgado.getMateria().getNombre());
    }

    @Test
    void update() {
        given(materiaRepository.findById(juzgado.getMateria().getId()))
                .willReturn(Optional.ofNullable(juzgado.getMateria()));
        given(sedeRepository.findById(juzgado.getSede().getId()))
                .willReturn(Optional.ofNullable(juzgado.getSede()));
        given(juzgadoRepository.save(juzgado))
                .willReturn(juzgado);
        given(juzgadoRepository.findById(juzgado.getId()))
                .willReturn(Optional.of(juzgado.setContadorAsignaciones(0)));

        JuzgadoRecordItem response = juzgadoService.update(juzgado);

        assertThat(response).isOfAnyClassIn(JuzgadoRecordItem.class)
                .hasFieldOrPropertyWithValue("id", juzgado.getId())
                .hasFieldOrPropertyWithValue("nombre", juzgado.getNombre())
                .hasFieldOrPropertyWithValue("estado", juzgado.getEstado())
                .hasFieldOrPropertyWithValue("materia", juzgado.getMateria().getNombre());
    }

    @Test
    void create_return_materias_notFound_exception() {
        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> juzgadoService.create(juzgado)
        );
        assertThat(assertThrows.getMessage()).contains("Materia no encontrada");
    }


    @Test
    void create_return_sede_notFound_exception() {
        given(materiaRepository.findById(juzgado.getMateria().getId()))
                .willReturn(Optional.ofNullable(juzgado.getMateria()));

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> juzgadoService.create(juzgado)
        );
        assertThat(assertThrows.getMessage()).contains("Sede no encontrada");
    }

    @Test
    void update_return_optimistic_exception() {
        given(materiaRepository.findById(juzgado.getMateria().getId()))
                .willReturn(Optional.ofNullable(juzgado.getMateria()));
        given(sedeRepository.findById(juzgado.getSede().getId()))
                .willReturn(Optional.ofNullable(juzgado.getSede()));
        given(juzgadoRepository.findById(juzgado.getId()))
                .willReturn(Optional.of(juzgado.setContadorAsignaciones(0)));
        given(juzgadoRepository.save(juzgado))
                .willThrow(org.springframework.dao.OptimisticLockingFailureException.class);

        InvalidVersionException assertThrows = assertThrows(
                InvalidVersionException.class,
                () -> juzgadoService.update(juzgado)
        );

        assertThat(assertThrows.getMessage()).contains(OPTIMISTIC_LOCKING_ERROR);
    }

    @Test
    void update_return_materias_notFound_exception() {
        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> juzgadoService.update(juzgado)
        );
        assertThat(assertThrows.getMessage()).contains("Materia no encontrada");
    }

    @Test
    void update_return_sede_notFound_exception() {
        given(materiaRepository.findById(juzgado.getMateria().getId()))
                .willReturn(Optional.ofNullable(juzgado.getMateria()));

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> juzgadoService.update(juzgado)
        );
        assertThat(assertThrows.getMessage()).contains("Sede no encontrada");
    }

    @Test
    void getJuzgadoFolios() {
        given(juzgadoFoliosRepository.findByJuzgadoAndTipoCarpeta(juzgado, TipoCarpeta.DEMANDA))
                .willReturn(Optional.ofNullable(juzgadoFolios));

        JuzgadoFolios response = juzgadoService.getJuzgadoFolios(juzgado, TipoCarpeta.DEMANDA);

        assertThat(response).isOfAnyClassIn(JuzgadoFolios.class)
                .hasFieldOrPropertyWithValue("id", juzgadoFolios.getId())
                .hasFieldOrPropertyWithValue("juzgado", juzgadoFolios.getJuzgado())
                .hasFieldOrPropertyWithValue("tipoCarpeta", juzgadoFolios.getTipoCarpeta())
                .hasFieldOrPropertyWithValue("value", juzgadoFolios.getValue())
                .hasFieldOrPropertyWithValue("year", juzgadoFolios.getYear());
    }

    @Test
    void getJuzgadoFolios_return_juzgado_notFound_exception() {

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> juzgadoService.getJuzgadoFolios(juzgado, TipoCarpeta.DEMANDA)
        );
        assertThat(assertThrows.getMessage()).contains("juzgadoFolio no encontrado");
    }

    @Test
    void checkYearJuzgadoFolios() {
        given(juzgadoFoliosRepository.save(juzgadoFolios))
                .willReturn(juzgadoFolios);

        juzgadoFolios.setYear(2023);
        JuzgadoFolios response = juzgadoService.checkYearJuzgadoFolios(juzgadoFolios);

        assertThat(response).isOfAnyClassIn(JuzgadoFolios.class)
                .hasFieldOrPropertyWithValue("id", juzgadoFolios.getId())
                .hasFieldOrPropertyWithValue("juzgado", juzgadoFolios.getJuzgado())
                .hasFieldOrPropertyWithValue("tipoCarpeta", juzgadoFolios.getTipoCarpeta())
                .hasFieldOrPropertyWithValue("value", juzgadoFolios.getValue())
                .hasFieldOrPropertyWithValue("year", juzgadoFolios.getYear());
        assertThat(response.getYear()).isEqualTo(LocalDate.now().getYear());
        assertThat(response.getValue()).isEqualTo(1);
    }

    @Test
    void increaseValueJuzgadoFolios() {
        given(juzgadoFoliosRepository.save(juzgadoFolios))
                .willReturn(juzgadoFolios);

        JuzgadoFolios response = juzgadoService.increaseValueJuzgadoFolios(juzgadoFolios);

        assertThat(response).isOfAnyClassIn(JuzgadoFolios.class)
                .hasFieldOrPropertyWithValue("id", juzgadoFolios.getId())
                .hasFieldOrPropertyWithValue("juzgado", juzgadoFolios.getJuzgado())
                .hasFieldOrPropertyWithValue("tipoCarpeta", juzgadoFolios.getTipoCarpeta())
                .hasFieldOrPropertyWithValue("value", juzgadoFolios.getValue())
                .hasFieldOrPropertyWithValue("year", juzgadoFolios.getYear());
        assertThat(response.getValue()).isEqualTo(2);
    }

    @Test
    void actualizar_carga() {
        Mockito.doNothing().when(juzgadoRepository).actualizarContadorAsignaciones(any());
        given(juzgadoRepository.sumContadorAsignacionesByMateria(any(Materia.class), any(InstanciaJuzgado.class))).willReturn(10);
        given(juzgadoRepository.sumMaxAsignacionesRondaByMateria(any(Materia.class), any(InstanciaJuzgado.class))).willReturn(10);
        given(juzgadoRepository.findJuzgadosMenosAsignaciones(any(Materia.class), any(InstanciaJuzgado.class))).willReturn(new ArrayList<>());
        Mockito.doNothing().when(juzgadoRepository).reiniciarContadorAsignaciones(any(Materia.class), any(InstanciaJuzgado.class));

        TipoCarpeta tipoCarpeta = TipoCarpeta.DEMANDA;
        juzgadoService.actualizarCarga(juzgado, tipoCarpeta);

        verify(juzgadoRepository, times(1)).sumContadorAsignacionesByMateria(any(Materia.class), any(InstanciaJuzgado.class));
        verify(juzgadoRepository, times(1)).sumMaxAsignacionesRondaByMateria(any(Materia.class), any(InstanciaJuzgado.class));
        verify(juzgadoRepository, times(1)).findJuzgadosMenosAsignaciones(any(Materia.class), any(InstanciaJuzgado.class));
        verify(juzgadoRepository, times(1)).reiniciarContadorAsignaciones(any(Materia.class), any(InstanciaJuzgado.class));
    }

    @Test
    void get_juzgado_exception() {
        given(juzgadoRepository.findJuzgadosMenosAsignaciones(any(Materia.class), any(InstanciaJuzgado.class))).willReturn(new ArrayList<>());
        given(juzgadoRepository.sumContadorAsignacionesByMateria(any(Materia.class), any(InstanciaJuzgado.class))).willReturn(10);
        given(juzgadoRepository.sumMaxAsignacionesRondaByMateria(any(Materia.class), any(InstanciaJuzgado.class))).willReturn(10);

        TipoJuicio tipoJuicio = juzgado.getTipoJuicios().get(0);

        TipoCarpeta tipoDemanda = TipoCarpeta.DEMANDA;
        NotFoundException exceptionDemanda = assertThrows(
                NotFoundException.class,
                () -> juzgadoService.getJuzgado(tipoJuicio, tipoDemanda)
        );
        assertThat(exceptionDemanda.getMessage()).contains("No se encontró un Juzgado de la materia");

        TipoCarpeta tipoApelacion = TipoCarpeta.APELACION;
        Materia materiaFamiliar = new Materia().setNombre("Familiar");
        tipoJuicio.setMateria(materiaFamiliar);
        NotFoundException exceptionApelacion = assertThrows(
                NotFoundException.class,
                () -> juzgadoService.getJuzgado(tipoJuicio, tipoApelacion)
        );
        assertThat(exceptionApelacion.getMessage()).contains("No hay sala disponible para asignar.");

        TipoCarpeta tipoExhorto = TipoCarpeta.EXHORTO;
        NotFoundException exceptionExhorto = assertThrows(
                NotFoundException.class,
                () -> juzgadoService.getJuzgado(tipoJuicio, tipoExhorto)
        );
        assertThat(exceptionExhorto.getMessage()).contains("No se encontró un Juzgado de la materia");
    }

    @Test
    void get_juzgado_aleatorio() {

        TipoJuicio tipoJuicio = juzgado.getTipoJuicios().get(0);

        List<Juzgado> juzgadoDemanda = Collections.singletonList(juzgado);
        given(juzgadoRepository.findJuzgadosMenosAsignaciones(any(Materia.class), any(InstanciaJuzgado.class))).willReturn(juzgadoDemanda);
        TipoCarpeta tipoDemanda = TipoCarpeta.DEMANDA;
        Juzgado resultJuzgadoDemanda = juzgadoService.getJuzgado(tipoJuicio, tipoDemanda);

        assertThat(resultJuzgadoDemanda).isEqualTo(juzgado);

        List<Juzgado> juzgadoApelacion = Collections.singletonList(juzgado);
        Juzgado juzgadoSegundaInstancia = juzgado.setInstanciaJuzgado(InstanciaJuzgado.SEGUNDA_INSTANCIA);
        given(juzgadoRepository.findJuzgadosMenosAsignaciones(any(Materia.class), any(InstanciaJuzgado.class))).willReturn(juzgadoApelacion);
        TipoCarpeta tipoApelacion = TipoCarpeta.APELACION;
        Juzgado resultJuzgadoApelacion = juzgadoService.getJuzgado(tipoJuicio, tipoApelacion);

        assertThat(resultJuzgadoApelacion).isEqualTo(juzgadoSegundaInstancia);

        List<Juzgado> juzgadoExhorto = Collections.singletonList(juzgado);
        Juzgado juzgadoNoAplica = juzgado.setInstanciaJuzgado(InstanciaJuzgado.NO_APLICA);
        given(juzgadoRepository.findJuzgadosMenosAsignaciones(any(Materia.class), any(InstanciaJuzgado.class))).willReturn(juzgadoExhorto);
        TipoCarpeta tipoExhorto = TipoCarpeta.EXHORTO;
        Juzgado resultJuzgadoExhorto = juzgadoService.getJuzgado(tipoJuicio, tipoExhorto);

        assertThat(resultJuzgadoExhorto).isEqualTo(juzgadoNoAplica);
    }

    @Test
    void getAllByEstadoAutocomplete_return_page() {
        List<JuzgadoRecordItem> listPage = Collections.singletonList(juzgadoRecordItem);
        Persona persona = PersonaSetUp.createPersona();
        given(juzgadoRepository.findAllByEstadoAutocomplete(any(), any(), anyString(), anyInt()))
                .willReturn(listPage);

        given(personaService.getAuditor()).willReturn(persona);
        List<JuzgadoRecordItem> page = juzgadoService.findAllByEstadoAutocomplete("");
        assertThat(page)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", juzgado.getId())
                .hasFieldOrPropertyWithValue("nombre", juzgado.getNombre())
                .hasFieldOrPropertyWithValue("estado", juzgado.getEstado())
                .hasFieldOrPropertyWithValue("materia", juzgado.getMateria().getNombre());
    }

    @Test
    void delete() {
        Mockito.doThrow(DataIntegrityViolationException.class).when(juzgadoRepository).deleteById(any());
        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> juzgadoService.delete(1)
        );
        assertThat(exception.getMessage()).contains(Messages.CONSTRAINT_ERROR);
    }

    @Test
    void update_status_success() {
        given(juzgadoRepository.findById(any())).willReturn(Optional.of(juzgado));

        JuzgadoRecordItem response = juzgadoService.updateStatus(juzgado.getId(), 1);
        assertThat(response).isOfAnyClassIn(JuzgadoRecordItem.class)
                .hasFieldOrPropertyWithValue("id", response.id())
                .hasFieldOrPropertyWithValue("nombre", response.nombre())
                .hasFieldOrPropertyWithValue("estado", response.estado());

    }

    @Test
    void getSalas(){
        Juzgado sala = JuzgadoSetUp.createJuzgado(MateriaSetUp.createMateria(), SedeSetUp.createSede())
                .setInstanciaJuzgado(InstanciaJuzgado.SEGUNDA_INSTANCIA)
                .setNombre("Sala Civil");

        List<JuzgadoRecordItem> salas = Collections.singletonList(
                new JuzgadoRecordItem(sala.getId(), sala.getNombre(), sala.getEstado(), sala.getSede().getNombre()));

        given(juzgadoRepository.findAllByInstancia(any())).willReturn(salas);
        salas = juzgadoService.findAllByInstancia(InstanciaJuzgado.SEGUNDA_INSTANCIA);

        assertThat(salas).isNotNull().anyMatch(s->s.nombre().equals(sala.getNombre()));
    }

    @Test
    void juzgadoActual(){
        Persona persona = PersonaSetUp.createPersona();
        persona.setJuzgado(juzgado);

        JuzgadoRecordItem juzgadoActual = new JuzgadoRecordItem(juzgado.getId(), juzgado.getNombre(), juzgado.getEstado(), juzgado.getNombre());

        given(personaService.getAuditor()).willReturn(persona);

        juzgadoActual = juzgadoService.getJuzgadoActual();

        assertThat(juzgadoActual).isNotNull().hasFieldOrPropertyWithValue("nombre", juzgado.getNombre());

    }
}
