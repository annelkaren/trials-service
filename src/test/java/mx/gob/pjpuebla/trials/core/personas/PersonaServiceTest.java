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
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
import mx.gob.pjpuebla.trials.core.usuarios.UsuarioService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static mx.gob.pjpuebla.trials.core.personas.PersonaSetUp.createPersona;
import static mx.gob.pjpuebla.trials.core.personas.PersonaSetUp.createPersonaRecord;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

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

    private Persona validPersona;
    private PersonaRecord validPersonaRecord;
    private Domicilio validDomicilio;
    private Escolaridad escolaridad;
    private EstadoCivil estadoCivil;
    private Juzgado juzgado;

    @BeforeEach
    public void setUp() {
        validPersonaRecord = createPersonaRecord();
        validPersona = createPersona();
        validDomicilio = DomicilioSetUp.createDomicilio();
        escolaridad = EscolaridadSetUp.createEscolaridad();
        estadoCivil = EstadoCivilSetUp.createEstadoCivil();
        juzgado = JuzgadoSetUp.createJuzgado(Estado.ACTIVE);
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
                        + validPersona.getApellidoPaterno() + " " + validPersona.getApellidoMaterno());
    }

    @Test
    void getById_return_persona() {
        RoleRecord roleRecord = new RoleRecord("JUEZ", "JUEZ");
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        given(mockPersonaRepository.findByIdAndEstadoIn(validPersona.getId(), estados))
                .willReturn(Optional.ofNullable(validPersonaRecord));
        given(roleService.getRolesByUserId(validPersonaRecord.usuario())).willReturn(Arrays.asList(roleRecord));

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
                () -> {
                    personaService.findById(personaId);
                }
        );

        assertThat(assertThrows.getMessage()).contains("Persona no encontrada");
    }

    @Test
    void getByCurp_return_persona() {
        RoleRecord roleRecord = new RoleRecord("JUEZ", "JUEZ");
        given(mockPersonaRepository.findByCurp(validPersona.getCurp()))
                .willReturn(Optional.ofNullable(validPersonaRecord));
        given(roleService.getRolesByUserId(validPersonaRecord.usuario())).willReturn(Arrays.asList(roleRecord));

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
                () -> {
                    personaService.findByCurp(curp);
                }
        );

        assertThat(assertThrows.getMessage()).contains("Persona no encontrada");
    }

    @Test
    void create() {
        List<String> roles = Arrays.asList("JUEZ");
        List<RoleRecord> rolesRecord = Arrays.asList(new RoleRecord("JUEZ", "JUEZ"));
        given(usuarioService.create(validPersona)).willReturn("usuario-valido");
        roleService.addRoles(validPersona.getUsuario(), roles);
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
    }

    @Test
    void update() {
        List<String> roles = Arrays.asList("JUEZ");
        List<RoleRecord> rolesRecord = Arrays.asList(new RoleRecord("JUEZ", "JUEZ"));
        Mockito.doNothing().when(roleService).updateRoles(validPersona.getUsuario(), roles);
        roleService.updateRoles(validPersona.getUsuario(), roles);
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
    }

    @Test
    void update_return_optimistic_exception() {
        List<String> roles = Arrays.asList("JUEZ");
        List<RoleRecord> rolesRecord = Arrays.asList(new RoleRecord("JUEZ", "JUEZ"));
        roleService.updateRoles(validPersona.getUsuario(), roles);
        given(escolaridadRepository.findById(escolaridad.getId())).willReturn(Optional.ofNullable(escolaridad));
        given(estadoCivilRepository.findById(estadoCivil.getId())).willReturn(Optional.ofNullable(estadoCivil));
        given(juzgadoRepository.findById(juzgado.getId())).willReturn(Optional.ofNullable(juzgado));
        given(domicilioService.save(validDomicilio)).willReturn(validDomicilio);
        given(mockPersonaRepository.save(validPersona))
                .willThrow(org.springframework.dao.OptimisticLockingFailureException.class);

        OptimisticLockingFailureException assertThrows = assertThrows(
                OptimisticLockingFailureException.class,
                () -> {
                    personaService.update(validPersona, rolesRecord);
                }
        );

        assertThat(assertThrows.getMessage()).contains("Persona modificada por otro usuario");
    }
}