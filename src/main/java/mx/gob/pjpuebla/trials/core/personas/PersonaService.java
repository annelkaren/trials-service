package mx.gob.pjpuebla.trials.core.personas;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioService;
import mx.gob.pjpuebla.trials.core.escolaridades.EscolaridadRepository;
import mx.gob.pjpuebla.trials.core.estadocivil.EstadoCivilRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecordItem;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.oficialias.OficialiaRepository;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
import mx.gob.pjpuebla.trials.core.salas.Sala;
import mx.gob.pjpuebla.trials.core.salas.SalaRepository;
import mx.gob.pjpuebla.trials.core.usuarios.UsuarioService;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.ExternalUser;
import mx.gob.pjpuebla.trials.util.enums.TipoCentroTrabajo;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class PersonaService {

    private final AuditorAware<Jwt> auditorAware;
    private final PersonaRepository personaRepository;
    private final DomicilioService domicilioService;
    private final EscolaridadRepository escolaridadRepository;
    private final EstadoCivilRepository estadoCivilRepository;
    private final JuzgadoRepository juzgadoRepository;
    private final OficialiaRepository oficialiaRepository;
    private final UsuarioService usuarioService;
    private final RoleService roleService;
    private final SalaRepository salaRepository;
    private static final String PERSON_NOT_FOUND = "Persona no encontrada";

    @Transactional(readOnly = true)
    public Page<PersonaRecordResponse> getAll(Persona example, Pageable pageable) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<Persona> page = personaRepository.findAll(Example.of(example, exampleMatcher), pageable);

        List<PersonaRecordResponse> list = page.getContent().stream()
                .map(persona -> {
                    String centroTrabajo;
                    if ((persona.getJuzgado() != null && persona.getJuzgado().getNombre() != null && !persona.getJuzgado().getNombre().isEmpty())) {
                        centroTrabajo = persona.getJuzgado().getNombre();
                    } else {
                        if (persona.getOficialia() != null && persona.getOficialia().getNombre() != null && !persona.getOficialia().getNombre().isEmpty())
                            centroTrabajo = persona.getOficialia().getNombre();
                        else centroTrabajo = "-";
                    }
                    return new PersonaRecordResponse(
                            persona.getId(),
                            persona.getNombre() + " " + persona.getApellidoPaterno() + (persona.getApellidoMaterno() != null ? " " + persona.getApellidoMaterno() : ""),
                            persona.getCorreoElectronico(),
                            persona.getCelular(),
                            centroTrabajo,
                            persona.getEstado().name(),
                            roleService.getRolesByUserId(persona.getUsuario()).get(0).name()
                    );
                })
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public PersonaRecord findById(Long id) {
        PersonaRecord persona = personaRepository.findByIdAndEstadoIn(id, Arrays.asList(Estado.INACTIVE, Estado.ACTIVE))
                .orElseThrow(() -> new NotFoundException(PERSON_NOT_FOUND, "personaId"));
        List<RoleRecord> roles = roleService.getRolesByUserId(persona.usuario());
        return persona.withRoles(roles);
    }

    public PersonaRecordResponse create(Persona persona, List<RoleRecord> roles) {
        if (!isValidAge(persona.getFechaNacimiento())) {
            throw new ConflictException("El usuario debe ser mayor de edad");
        }
        List<String> rolesToSave = getNames(roles);
        validateAdminRole(rolesToSave, persona);
        persona.setUsuario(usuarioService.create(persona));
        persona.setIsExternalUser(ExternalUser.NO);
        roleService.addRoles(persona.getUsuario(), rolesToSave);

        fillPersonaData(persona);

        persona = personaRepository.save(persona);
        return new PersonaRecordResponse(persona.getId(), persona.getNombre(), persona.getCorreoElectronico(), persona.getCelular(), "", "", "");
    }

    private boolean isValidAge(LocalDate date) {
        LocalDate currentDate = LocalDate.now();
        long numberOfYears = ChronoUnit.YEARS.between(date, currentDate);
        return numberOfYears >= 18;
    }

    private void fillPersonaData(Persona persona) {
        persona.setEscolaridad(escolaridadRepository.findById(persona.getEscolaridad().getId())
                .orElseThrow(() -> new NotFoundException("Escolaridad no encontrada", "escolaridadId")));
        persona.setEstadoCivil(estadoCivilRepository.findById(persona.getEstadoCivil().getId())
                .orElseThrow(() -> new NotFoundException("Estado Civil no encontrado", "estadoCivilId")));
        persona.setDomicilio(domicilioService.save(persona.getDomicilio()));

        if (persona.getJuzgado() != null && persona.getJuzgado().getId() != null) {
            persona.setJuzgado(juzgadoRepository.findById(persona.getJuzgado().getId())
                    .orElseThrow(() -> new NotFoundException("Juzgado no encontrado", "juzgadoId")));
        } else {
            persona.setJuzgado(null);
        }

        if (persona.getOficialia() != null && persona.getOficialia().getId() != null) {
            persona.setOficialia(oficialiaRepository.findById(persona.getOficialia().getId())
                    .orElseThrow(() -> new NotFoundException("Oficialia no encontrada", "oficialiaId")));
        } else {
            persona.setOficialia(null);
        }
    }

    public PersonaRecordResponse update(Persona persona, List<RoleRecord> roles) {
        try {
            if (!isValidAge(persona.getFechaNacimiento())) {
                throw new ConflictException("El usuario debe ser mayor de edad");
            }
            List<String> rolesToSave = getNames(roles);
            validateAdminRole(rolesToSave, persona);
            fillPersonaData(persona);
            persona = personaRepository.save(persona);
            roleService.updateRoles(persona.getUsuario(), rolesToSave);
            return new PersonaRecordResponse(persona.getId(), persona.getNombre(), persona.getCorreoElectronico(), persona.getCelular(), "", "", "");
        } catch (OptimisticLockingFailureException ex) {
            throw new InvalidVersionException(Persona.class.getSimpleName());
        }
    }

    @Transactional(readOnly = true)
    public PersonaRecord findByCurp(String curp) {
        PersonaRecord persona = personaRepository.findByCurp(curp)
                .orElseThrow(() -> new NotFoundException(PERSON_NOT_FOUND, "curp"));
        List<RoleRecord> roles = roleService.getRolesByUserId(persona.usuario());
        return persona.withRoles(roles);
    }

    private List<String> getNames(List<RoleRecord> list) {
        List<String> roles = new ArrayList<>();
        list.forEach(roleRecord -> roles.add(roleRecord.id()));
        return roles;
    }

    @Transactional(readOnly = true)
    public List<JuezRecord> findAllJueces(Integer juzgadoId) {
        List<Sala> salas;
        List<JuezRecord> jueces = new ArrayList<>();
        List<String> roles = Arrays.asList("JUEZ", "SECRETARIO");
        List<String> ids = usuarioService.findAllByRoles(roles);
        for (String id : ids) {
            Optional<Persona> juez = personaRepository.findByUsuarioAndJuzgadoIdAndEstadoIn(id, juzgadoId, List.of(Estado.ACTIVE));
            if (juez.isPresent()) {
                salas = salaRepository.findAllByJuezId(juez.get().getId());
                if (salas.isEmpty()) {
                    String name = juez.get().getNombre() + " " + juez.get().getApellidoPaterno();
                    name += ((juez.get().getApellidoMaterno() != null) ? " " + juez.get().getApellidoMaterno() : "");
                    JuezRecord juezRecord = new JuezRecord(juez.get().getId(), name);
                    jueces.add(juezRecord);
                }
            }
        }
        return jueces;
    }

    @Transactional(readOnly = true)
    public List<EncargadoCarritoRecord> findAllEncargadosCarrito() {
        List<EncargadoCarritoRecord> encargadoCarritoList = new ArrayList<>();
        List<String> ids = usuarioService.findAllByRoles(List.of("ENCARGADO_CARRITO"));
        for (String id : ids) {
            Optional<Persona> persona = personaRepository.findByUsuario(id);
            if (persona.isPresent()) {
                String name = persona.get().getNombre() + " " + persona.get().getApellidoPaterno();
                name += ((persona.get().getApellidoMaterno() != null) ? " " + persona.get().getApellidoMaterno() : "");
                EncargadoCarritoRecord encargadoCarritoRecord = new EncargadoCarritoRecord(persona.get().getId(), name);
                encargadoCarritoList.add(encargadoCarritoRecord);
            }
        }
        return encargadoCarritoList;
    }

    @Transactional(readOnly = true)
    public List<CentroTrabajoRecord> findAllCentroTrabajo(String nombre) {
        Persona currentUser = getAuditor();

        nombre = (nombre != null) ? nombre.toLowerCase() : "";
        List<CentroTrabajoRecord> centrosTrabajo = new ArrayList<>();

        if (roleService.hasRole(currentUser.getUsuario(), "ADMINISTRADOR_SISTEMA")) {
            List<JuzgadoRecordItem> juzgados = juzgadoRepository.findAllByEstadoAutocomplete(Estado.ACTIVE, nombre);

            List<Oficialia> oficialias = oficialiaRepository.findAllByEstadoAutocomplete(Estado.ACTIVE, nombre);

            for (JuzgadoRecordItem juzgado : juzgados) {
                centrosTrabajo.add(new CentroTrabajoRecord(juzgado.id(), juzgado.nombre(), TipoCentroTrabajo.JUZGADO));
            }

            for (Oficialia oficialia : oficialias) {
                centrosTrabajo.add(new CentroTrabajoRecord(oficialia.getId(), oficialia.getNombre(), TipoCentroTrabajo.OFICIALIA_COMUN));
            }
        } else {
            centrosTrabajo.add(new CentroTrabajoRecord(currentUser.getJuzgado().getId(), currentUser.getJuzgado().getNombre(), TipoCentroTrabajo.JUZGADO));
        }

        return centrosTrabajo;
    }

    @Transactional(readOnly = true)
    public Page<PersonaRecordResponse> findAllByCentroTrabajo(String nombre,String searchQuery, Pageable pageable) {
        Persona usuario = getAuditor();
        boolean adminSistema = roleService.hasRole(usuario.getUsuario(), "ADMINISTRADOR_SISTEMA");
        Page<Persona> page = personaRepository.findByCentroTrabajoAndSearch(
                searchQuery,
                usuario.getOficialia() != null ? usuario.getOficialia().getId() : null,
                usuario.getJuzgado() != null ? usuario.getJuzgado().getId() : null,
                adminSistema,
                pageable
        );

        List<PersonaRecordResponse> list = page.stream()
                .filter(p -> p.getNombre().contains(nombre == null ? "" : nombre))
                .map(p ->
                        new PersonaRecordResponse(
                                p.getId(),
                                p.getNombre() + " " + p.getApellidoPaterno() + (p.getApellidoMaterno() == null ? "" : " " + p.getApellidoMaterno()),
                                p.getCorreoElectronico(),
                                p.getCelular(),
                                (p.getJuzgado() != null) ? p.getJuzgado().getNombre() :
                                        (p.getOficialia() != null) ? p.getOficialia().getNombre() : "-",
                                p.getEstado().name(),
                                ""
                        ))
                .toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Persona getAuditor() {
        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();
        return personaRepository.findByUsuario(jwt.getSubject()).orElseThrow(() -> new NotFoundException(PERSON_NOT_FOUND, "usuario: " + jwt.getSubject()));
    }

    @Transactional(readOnly = true)
    public List<PersonaRecordResponse> getPersonalTurnado() {
        Persona persona = getAuditor();
        List<Persona> list = new ArrayList<>();
        Integer juzgadoId = persona.getJuzgado() != null ? persona.getJuzgado().getId() : null;

        List<Persona> personasDelJuzgado = personaRepository.findByJuzgadoId(juzgadoId);

        for (Persona item : personasDelJuzgado) {
            if (roleService.hasRole(item.getUsuario(), "ADMINISTRADOR_JUZGADO") ||
                roleService.hasRole(item.getUsuario(), "AUXILIAR_OFICIAL_MAYOR_JUZGADO") ||
                persona.getUsuario().equals(item.getUsuario()) ||
                roleService.getRolesByUserId(item.getUsuario()).isEmpty()
                ) {
                list.add(item);
            }
        }

        if (!list.isEmpty())
            personasDelJuzgado.removeAll(list);

        return personasDelJuzgado.stream().map(p -> new PersonaRecordResponse(
                p.getId(),
                p.getNombre() + " " + p.getApellidoPaterno() + (p.getApellidoMaterno() != null ? " " + p.getApellidoMaterno() : ""),
                p.getCorreoElectronico(),
                p.getCelular(),
                "",
                "",
                roleService.getRolesByUserId(p.getUsuario()).get(0).name()
        )).toList();
    }

    private void validateAdminRole(List<String> rolesToSave, Persona persona) {
        boolean hasAdminRole = rolesToSave.stream().anyMatch(r -> r.equalsIgnoreCase("ADMINISTRADOR_SISTEMA"));
        if (hasAdminRole && (
                (persona.getJuzgado() != null && persona.getJuzgado().getId() != null)
                        || (persona.getOficialia() != null && persona.getOficialia().getId() != null))) {
            throw new ConflictException("Un Administrador (sistema) no puede pertenecer a un centro de trabajo");
        }
        if (hasAdminRole && rolesToSave.size() > 1) {
            throw new ConflictException("Un Administrador (sistema) no puede tener más roles asociados");
        }

        if (!hasAdminRole && (
                (persona.getJuzgado() == null || persona.getJuzgado().getId() == null)
                        && (persona.getOficialia() == null || persona.getOficialia().getId() == null))
                && !rolesToSave.isEmpty()) {
            throw new ConflictException("Seleccione un centro de trabajo para asignar los roles correspondientes");
        }
    }

    public boolean verifyIfUserExistsAndIsLitigante(String username) {
        String usuario = usuarioService.findByUsernameAndRol(username, "LITIGANTE");
        Optional<Persona> persona = personaRepository.findByUsuario(usuario);
        if (persona.isPresent()) {
            return persona.get().getIsExternalUser().equals(ExternalUser.YES);
        } else {
            throw new NotFoundException(PERSON_NOT_FOUND, username);
        }
    }
}