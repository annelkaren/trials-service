package mx.gob.pjpuebla.trials.core.personas;

import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioService;
import mx.gob.pjpuebla.trials.core.escolaridades.Escolaridad;
import mx.gob.pjpuebla.trials.core.escolaridades.EscolaridadRepository;
import mx.gob.pjpuebla.trials.core.escolaridades.EscolaridadSetUp;
import mx.gob.pjpuebla.trials.core.estadocivil.EstadoCivil;
import mx.gob.pjpuebla.trials.core.estadocivil.EstadoCivilRepository;
import mx.gob.pjpuebla.trials.core.estadocivil.EstadoCivilSetUp;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.oficialias.OficialiaRepository;
import mx.gob.pjpuebla.trials.core.oficialias.OficialiaSetUp;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
import mx.gob.pjpuebla.trials.core.salas.Sala;
import mx.gob.pjpuebla.trials.core.salas.SalaRepository;
import mx.gob.pjpuebla.trials.core.salas.SalaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaSetUp;
import mx.gob.pjpuebla.trials.core.usuarios.UsuarioService;
import mx.gob.pjpuebla.trials.core.utils.audit.SetupServiceTest;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.TipoCentroTrabajo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

import static mx.gob.pjpuebla.trials.core.personas.PersonaSetUp.createPersona;
import static mx.gob.pjpuebla.trials.core.personas.PersonaSetUp.createPersonaRecord;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PersonaServiceTest extends SetupServiceTest {

    @Mock
    PersonaRepository mockPersonaRepository;
    @Mock
    JuzgadoRepository juzgadoRepository;
    @Mock
    EscolaridadRepository escolaridadRepository;
    @Mock
    EstadoCivilRepository estadoCivilRepository;
    @InjectMocks
    PersonaService personaService;
    @Mock
    DomicilioService domicilioService;
    @Mock
    UsuarioService usuarioService;
    @Mock
    RoleService roleService;
    @Mock
    SalaRepository salaRepository;
    @Mock
    OficialiaRepository oficialiaRepository;

    private Persona validPersona;
    private PersonaRecord validPersonaRecord;
    private Domicilio validDomicilio;
    private Escolaridad escolaridad;
    private EstadoCivil estadoCivil;
    private Juzgado juzgado;
    private Oficialia oficialia;

    @BeforeEach
    public void setUp() {
        validPersonaRecord = createPersonaRecord();
        validPersona = createPersona();
        validDomicilio = DomicilioSetUp.createDomicilio();
        escolaridad = EscolaridadSetUp.createEscolaridad();
        estadoCivil = EstadoCivilSetUp.createEstadoCivil();
        juzgado = JuzgadoSetUp.createJuzgado(Estado.ACTIVE);
        oficialia = OficialiaSetUp.createOficialia(TipoOficialiaSetUp.createtipoOficialia(), SedeSetUp.createSede());
        validPersona.setEscolaridad(escolaridad);
        validPersona.setEstadoCivil(estadoCivil);
        validPersona.setJuzgado(juzgado);
        validPersona.setDomicilio(validDomicilio);
    }

    @Test
    void getAll_return_page() {
        List<Persona> listPage = Collections.singletonList(validPersona);
        given(mockPersonaRepository.findAll(any(Example.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<PersonaRecordResponse> page = personaService.getAll(validPersona, PageRequest.of(1, listPage.size()));
        assertThat(page.getContent())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", validPersona.getId())
                .hasFieldOrPropertyWithValue("nombre", validPersona.getNombre() + " "
                        + validPersona.getApellidoPaterno());
    }

    @Test
    void getById_return_persona() {
        RoleRecord roleRecord = new RoleRecord("JUEZ", "JUEZ");
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(mockPersonaRepository.findByIdAndEstadoIn(validPersona.getId(), estados))
                .willReturn(Optional.ofNullable(validPersonaRecord));
        given(roleService.getRolesByUserId(validPersonaRecord.usuario())).willReturn(List.of(roleRecord));

        PersonaRecord mr = personaService.findById(validPersonaRecord.id());
        assertThat(mr).isOfAnyClassIn(PersonaRecord.class)
                .hasFieldOrPropertyWithValue("id", validPersonaRecord.id())
                .hasFieldOrPropertyWithValue("nombre", validPersonaRecord.nombre())
                .hasFieldOrProperty("permisos").isNotNull();
    }

    @Test
    void getById_return_not_found() {
        Long personaId = validPersona.getId();
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(mockPersonaRepository.findByIdAndEstadoIn(personaId, estados))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> personaService.findById(personaId)
        );

        assertThat(assertThrows.getMessage()).contains("Persona no encontrada");
    }

    @Test
    void getByCurp_return_persona() {
        RoleRecord roleRecord = new RoleRecord("JUEZ", "JUEZ");
        given(mockPersonaRepository.findByCurp(validPersona.getCurp()))
                .willReturn(Optional.ofNullable(validPersonaRecord));
        given(roleService.getRolesByUserId(validPersonaRecord.usuario())).willReturn(List.of(roleRecord));

        PersonaRecord mr = personaService.findByCurp(validPersona.getCurp());
        assertThat(mr).isOfAnyClassIn(PersonaRecord.class)
                .hasFieldOrPropertyWithValue("id", validPersonaRecord.id())
                .hasFieldOrPropertyWithValue("nombre", validPersonaRecord.nombre())
                .hasFieldOrProperty("permisos").isNotNull();
    }

    @Test
    void getByCurp_return_not_found() {
        String curp = validPersona.getCurp();
        given(mockPersonaRepository.findByCurp(curp))
                .willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> personaService.findByCurp(curp)
        );

        assertThat(assertThrows.getMessage()).contains("Persona no encontrada");
    }

    @Test
    void create() {
        List<String> roles = List.of("JUEZ");
        List<RoleRecord> rolesRecord = List.of(new RoleRecord("JUEZ", "JUEZ"));
        given(usuarioService.create(validPersona)).willReturn("usuario-valido");
        roleService.addRoles(validPersona.getUsuario(), roles);
        assertThat(validPersona).hasFieldOrPropertyWithValue("juzgado", juzgado);

        given(escolaridadRepository.findById(escolaridad.getId())).willReturn(Optional.ofNullable(escolaridad));
        given(estadoCivilRepository.findById(estadoCivil.getId())).willReturn(Optional.ofNullable(estadoCivil));
        given(juzgadoRepository.findById(juzgado.getId())).willReturn(Optional.ofNullable(juzgado));
        given(domicilioService.save(validDomicilio)).willReturn(validDomicilio);
        given(mockPersonaRepository.save(validPersona)).willReturn(validPersona);

        PersonaRecordResponse response = personaService.create(validPersona, rolesRecord);

        assertThat(response).isOfAnyClassIn(PersonaRecordResponse.class)
                .hasFieldOrPropertyWithValue("id", validPersona.getId())
                .hasFieldOrPropertyWithValue("nombre", validPersona.getNombre())
                .hasFieldOrPropertyWithValue("email", validPersona.getCorreoElectronico());

        assertThat(validPersona).hasFieldOrPropertyWithValue("juzgado", juzgado)
                .hasFieldOrPropertyWithValue("oficialia", null);

        validPersona.setJuzgado(null);
        validPersona.setOficialia(oficialia);

        mockPersonaRepository.save(validPersona);

        assertThat(validPersona).hasFieldOrPropertyWithValue("oficialia", oficialia)
                .hasFieldOrPropertyWithValue("juzgado", null);

    }

    @Test
    void update() {
        List<String> roles = List.of("JUEZ");
        List<RoleRecord> rolesRecord = List.of(new RoleRecord("JUEZ", "JUEZ"));
        Mockito.doNothing().when(roleService).updateRoles(validPersona.getUsuario(), roles);
        roleService.updateRoles(validPersona.getUsuario(), roles);
        validPersona.setJuzgado(juzgado);

        given(escolaridadRepository.findById(escolaridad.getId())).willReturn(Optional.ofNullable(escolaridad));
        given(estadoCivilRepository.findById(estadoCivil.getId())).willReturn(Optional.ofNullable(estadoCivil));
        given(juzgadoRepository.findById(juzgado.getId())).willReturn(Optional.ofNullable(juzgado));
        given(domicilioService.save(validDomicilio)).willReturn(validDomicilio);
        given(mockPersonaRepository.save(validPersona)).willReturn(validPersona);


        PersonaRecordResponse response = personaService.update(validPersona, rolesRecord);

        assertThat(response).isOfAnyClassIn(PersonaRecordResponse.class)
                .hasFieldOrPropertyWithValue("id", validPersona.getId())
                .hasFieldOrPropertyWithValue("nombre", validPersona.getNombre())
                .hasFieldOrPropertyWithValue("email", validPersona.getCorreoElectronico());

        assertThat(validPersona).hasFieldOrPropertyWithValue("juzgado", juzgado)
                .hasFieldOrPropertyWithValue("oficialia", null);

        validPersona.setJuzgado(null);
        validPersona.setOficialia(oficialia);

        mockPersonaRepository.save(validPersona);

        assertThat(validPersona).hasFieldOrPropertyWithValue("oficialia", oficialia)
                .hasFieldOrPropertyWithValue("juzgado", null);
    }

    @Test
    void update_return_optimistic_exception() {
        List<String> roles = List.of("JUEZ");
        List<RoleRecord> rolesRecord = List.of(new RoleRecord("JUEZ", "JUEZ"));
        roleService.updateRoles(validPersona.getUsuario(), roles);
        given(escolaridadRepository.findById(escolaridad.getId())).willReturn(Optional.ofNullable(escolaridad));
        given(estadoCivilRepository.findById(estadoCivil.getId())).willReturn(Optional.ofNullable(estadoCivil));
        given(juzgadoRepository.findById(juzgado.getId())).willReturn(Optional.ofNullable(juzgado));
        given(domicilioService.save(validDomicilio)).willReturn(validDomicilio);
        given(mockPersonaRepository.save(validPersona))
                .willThrow(OptimisticLockingFailureException.class);

        InvalidVersionException assertThrows = assertThrows(
                InvalidVersionException.class,
                () -> personaService.update(validPersona, rolesRecord)
        );

        assertThat(assertThrows.getMessage()).contains("Version modificada por otro usuario");
    }

    @Test
    void getAll_jueces_return_empty_list() {
        Sala sala = SalaSetUp.createSala(Estado.ACTIVE);
        given(usuarioService.findAllByRoles(any())).willReturn(List.of("6b13785f-d213-4585-a76b-437ffe57c9c7"));
        given(mockPersonaRepository.findByUsuarioAndJuzgadoIdAndEstadoIn(any(), any(), any())).willReturn(Optional.of(validPersona));
        given(salaRepository.findAllByJuezId(validPersona.getId())).willReturn(List.of(sala));

        List<JuezRecord> jueces = personaService.findAllJueces(juzgado.getId());
        assertThat(jueces).isEmpty();
    }

    @Test
    void getAll_jueces_return_list() {
        given(usuarioService.findAllByRoles(any())).willReturn(List.of("6b13785f-d213-4585-a76b-437ffe57c9c7"));
        given(mockPersonaRepository.findByUsuarioAndJuzgadoIdAndEstadoIn(any(), any(), any())).willReturn(Optional.of(validPersona));
        given(salaRepository.findAllByJuezId(validPersona.getId())).willReturn(new ArrayList<>());

        List<JuezRecord> jueces = personaService.findAllJueces(juzgado.getId());
        String name = validPersona.getNombre() + " " + validPersona.getApellidoPaterno();
        name += ((validPersona.getApellidoMaterno() != null) ? " " + validPersona.getApellidoMaterno() : "");
        assertThat(jueces)
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", validPersona.getId())
                .hasFieldOrPropertyWithValue("nombreCompleto", name);
    }

    @Test
    void getAll_CentrosTrabajo_adminJuzgados() {
        given(mockPersonaRepository.findByUsuario(any())).willReturn(Optional.of(validPersona));
        given(juzgadoRepository.findAllByEstadoAutocomplete(Estado.ACTIVE, "", "Juzgado", 1)).willReturn(Arrays.asList(JuzgadoSetUp.createJuzgadoRecordResponse(juzgado, "TEST")));
        given(oficialiaRepository.findAllByEstadoAutocomplete(Estado.ACTIVE, "")).willReturn(Arrays.asList(oficialia));

        List<CentroTrabajoRecord> centrosTrabajo = personaService.findAllCentroTrabajo("");

        assertThat(centrosTrabajo)
                .hasSize(1)
                .anyMatch(centro -> centro.tipo().equals(TipoCentroTrabajo.JUZGADO));
    }

    @Test
    void getAll_CentrosTrabajo_adminSistemas() {
        given(roleService.hasRole(any(), any())).willReturn(true);
        given(mockPersonaRepository.findByUsuario(any())).willReturn(Optional.of(validPersona));
        given(juzgadoRepository.findAllByEstadoAutocomplete(Estado.ACTIVE, "", "Juzgado", 1)).willReturn(Arrays.asList(JuzgadoSetUp.createJuzgadoRecordResponse(juzgado, "TEST")));
        given(oficialiaRepository.findAllByEstadoAutocomplete(Estado.ACTIVE, "")).willReturn(Arrays.asList(oficialia));

        List<CentroTrabajoRecord> centrosTrabajo = personaService.findAllCentroTrabajo("");

        assertThat(centrosTrabajo)
                .hasSize(2)
                .anyMatch(centro -> centro.tipo().equals(TipoCentroTrabajo.JUZGADO))
                .anyMatch(centro -> centro.tipo().equals(TipoCentroTrabajo.OFICIALIA_COMUN));
    }

    @Test
    void getAuditor() {
        given(mockPersonaRepository.findByUsuario(any())).willReturn(Optional.of(validPersona));
        Persona result = personaService.getAuditor();
        assertThat(result).isNotNull();
    }

    @Test
    void getAll_encargados_carrito() {
        given(usuarioService.findAllByRoles(any())).willReturn(List.of("6b13785f-d213-4585-a76b-437ffe57c9c7"));
        given(mockPersonaRepository.findByUsuario(any())).willReturn(Optional.of(validPersona));

        List<EncargadoCarritoRecord> encargadoCarritoRecordList = personaService.findAllEncargadosCarrito();
        String name = validPersona.getNombre() + " " + validPersona.getApellidoPaterno();
        name += ((validPersona.getApellidoMaterno() != null) ? " " + validPersona.getApellidoMaterno() : "");
        assertThat(encargadoCarritoRecordList)
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", validPersona.getId())
                .hasFieldOrPropertyWithValue("nombreCompleto", name);
    }

    @Test
    void findAllByCentroTrabajo(){
        Jwt mockJwt = Mockito.mock(Jwt.class);
        when(mockJwt.getSubject()).thenReturn(validPersona.getUsuario());

        Page<Persona> page = new PageImpl<>(List.of(validPersona));
        List<PersonaRecordResponse> list = page.stream().map(p-> new PersonaRecordResponse(p.getId(), p.getNombre(), p.getCorreoElectronico(), p.getCelular(), "")).toList();
        Page<PersonaRecordResponse> response = new PageImpl<>(list);

        given(mockPersonaRepository.findByUsuario(any())).willReturn(Optional.of(validPersona));
        given(mockPersonaRepository.findByCentroTrabajo(any(), any(), any())).willReturn(page);

        response = personaService.findAllByCentroTrabajo(null, PageRequest.of(0, response.getSize()));

        assertThat(response).isNotEmpty();
    }

    @Test
    void getPersonalTurnado_return_page_of_personaRecordResponse() {
        Jwt mockJwt = Mockito.mock(Jwt.class);
        when(mockJwt.getSubject()).thenReturn(validPersona.getUsuario());

        given(mockPersonaRepository.findByUsuario(any())).willReturn(Optional.of(validPersona));

        given(mockPersonaRepository.findByJuzgadoId(eq(juzgado.getId()), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(validPersona), PageRequest.of(0, 10), 1));

        Page<PersonaRecordResponse> response = personaService.getPersonalTurnado(PageRequest.of(0, 10));

        assertThat(response).isNotEmpty();
        assertThat(response.getContent()).hasSize(1);
    }
}