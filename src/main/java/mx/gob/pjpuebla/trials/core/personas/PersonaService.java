package mx.gob.pjpuebla.trials.core.personas;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioService;
import mx.gob.pjpuebla.trials.core.escolaridades.EscolaridadRepository;
import mx.gob.pjpuebla.trials.core.estadocivil.EstadoCivilRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecordItem;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.oficialias.OficialiaRepository;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
import mx.gob.pjpuebla.trials.core.salas.Sala;
import mx.gob.pjpuebla.trials.core.salas.SalaRepository;
import mx.gob.pjpuebla.trials.core.usuarios.UsuarioService;
import mx.gob.pjpuebla.trials.error.ApiResponse;
import mx.gob.pjpuebla.trials.error.ApiResponseFactory;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.ExternalUser;
import mx.gob.pjpuebla.trials.util.enums.Sexo;
import mx.gob.pjpuebla.trials.util.enums.TipoCentroTrabajo;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import mx.gob.pjpuebla.trials.config.KeycloakSecurityUtil;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(transactionManager = "primaryTransactionManager")
public class PersonaService {

    private final AuditorAware<Jwt> auditorAware;
    private final PersonaRepository personaRepository;
    private final DomicilioService domicilioService;
    private final EscolaridadRepository escolaridadRepository;
    private final EstadoCivilRepository estadoCivilRepository;
    private final JuzgadoRepository juzgadoRepository;
    private final OficialiaRepository oficialiaRepository;
    private final UsuarioService usuarioService;
    private final MateriaRepository materiaRepository;
    private final RoleService roleService;
    private final SalaRepository salaRepository;
    private static final String PERSON_NOT_FOUND = "Persona no encontrada";
    private final KeycloakSecurityUtil keycloakSecurityUtil;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.get-token-url}")
    private String serverUrlKc;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Transactional(transactionManager = "primaryTransactionManager")
    public Page<PersonaRecordResponse> getAll(Persona example, Pageable pageable) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<Persona> page = personaRepository.findAll(Example.of(example, exampleMatcher), pageable);

        List<PersonaRecordResponse> list = page.getContent().stream()
                .map(persona -> {
                    String centroTrabajo;
                    if ((persona.getJuzgado() != null && persona.getJuzgado().getNombre() != null
                            && !persona.getJuzgado().getNombre().isEmpty())) {
                        centroTrabajo = persona.getJuzgado().getNombre();
                    } else {
                        if (persona.getOficialia() != null && persona.getOficialia().getNombre() != null
                                && !persona.getOficialia().getNombre().isEmpty())
                            centroTrabajo = persona.getOficialia().getNombre();
                        else
                            centroTrabajo = "-";
                    }
                    return new PersonaRecordResponse(
                            persona.getId(),
                            persona.getNombre() + " " + persona.getApellidoPaterno()
                                    + (persona.getApellidoMaterno() != null ? " " + persona.getApellidoMaterno() : ""),
                            persona.getCorreoElectronico(),
                            persona.getCelular(),
                            centroTrabajo,
                            persona.getEstado().name(),
                            roleService.getRolesByUserId(persona.getUsuario()).get(0).name());
                })
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(transactionManager = "primaryTransactionManager")
    public PersonaRecord findById(Long id) {
        String tipoCentroTrabajo = "";
        Integer centroTrabajoId = null;
        PersonaRecord persona = personaRepository.findByIdAndEstadoIn(id, Arrays.asList(Estado.INACTIVE, Estado.ACTIVE))
                .orElseThrow(() -> new NotFoundException(PERSON_NOT_FOUND, "personaId"));
        if (persona.juzgadoId() != null) {
            tipoCentroTrabajo = "JUZGADO";
            centroTrabajoId = persona.juzgadoId();
        }
        if (persona.oficialiaId() != null) {
            tipoCentroTrabajo = "OFICIALIA_COMUN";
            centroTrabajoId = persona.oficialiaId();
        }
        List<RoleRecord> roles = roleService.getRolesByUserId(persona.usuario(), tipoCentroTrabajo, centroTrabajoId);
        return persona.withRoles(roles);
    }

    public Optional<Persona> findPersonaById(Long personaId) {
        return personaRepository.findById(personaId);

    }

    public PersonaRecordResponse create(PersonaDTO dto) {
        Persona persona = dto.getPersona();
        List<RoleRecord> roles = dto.getRoles();
        if (!isValidAge(persona.getFechaNacimiento())) {
            throw new ConflictException("El usuario debe ser mayor de edad");
        }
        List<String> rolesToSave = getNames(roles);
        validateAdminRole(rolesToSave, persona);
        persona.setRolPrincipal(setRolPrincipal(roles, dto.getRolPrincipal()));
        persona.setUsuario(usuarioService.create(persona));
        persona.setIsExternalUser(ExternalUser.NO);
        roleService.addRoles(persona.getUsuario(), rolesToSave);

        fillPersonaData(persona);

        persona = personaRepository.save(persona);
        return new PersonaRecordResponse(persona.getId(), persona.getNombre(), persona.getCorreoElectronico(),
                persona.getCelular(), "", "", "");
    }

    private boolean isValidAge(LocalDate date) {
        LocalDate currentDate = LocalDate.now();
        long numberOfYears = ChronoUnit.YEARS.between(date, currentDate);
        return numberOfYears >= 18;
    }

    private void fillPersonaData(Persona persona) {
        persona.setEscolaridad(escolaridadRepository.findById(persona.getEscolaridad().getId())
                .orElseThrow(() -> new NotFoundException("Escolaridad no encontrada", "escolaridadId")));

        if (persona.getEstadoCivil().getId() != null) {
            persona.setEstadoCivil(estadoCivilRepository.findById(persona.getEstadoCivil().getId())
                    .orElseThrow(() -> new NotFoundException("Estado Civil no encontrado", "estadoCivilId")));
        } else {
            persona.setEstadoCivil(null);
        }

        if (persona.getDomicilio().getCalle() != null && !persona.getDomicilio().getCalle().isEmpty()) {
            persona.setDomicilio(domicilioService.save(persona.getDomicilio()));
        } else {
            persona.setDomicilio(null);
        }

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

    public PersonaRecordResponse update(PersonaDTO dto) {
        Persona persona = dto.getPersona();
        List<RoleRecord> roles = dto.getRoles();
        try {
            if (!isValidAge(persona.getFechaNacimiento())) {
                throw new ConflictException("El usuario debe ser mayor de edad");
            }
            persona.setIsExternalUser(ExternalUser.NO);
            List<String> rolesToSave = getNames(roles);
            validateAdminRole(rolesToSave, persona);
            fillPersonaData(persona);
            persona.setRolPrincipal(setRolPrincipal(roles, dto.getRolPrincipal()));
            persona = personaRepository.save(persona);
            roleService.updateRoles(persona.getUsuario(), rolesToSave);
            return new PersonaRecordResponse(persona.getId(), persona.getNombre(), persona.getCorreoElectronico(),
                    persona.getCelular(), "", "", "");
        } catch (OptimisticLockingFailureException ex) {
            throw new InvalidVersionException(Persona.class.getSimpleName());
        }
    }

    @Transactional(transactionManager = "primaryTransactionManager")
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

    @Transactional(transactionManager = "primaryTransactionManager")
    public List<JuezRecord> findAllJueces(Integer juzgadoId) {
        List<Sala> salas;
        List<JuezRecord> jueces = new ArrayList<>();
        List<String> roles = Arrays.asList("JUEZ", "SECRETARIO");
        List<String> ids = usuarioService.findAllByRoles(roles);
        for (String id : ids) {
            Optional<Persona> juez = personaRepository.findByUsuarioAndJuzgadoIdAndEstadoIn(id, juzgadoId,
                    List.of(Estado.ACTIVE));
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

    @Transactional(transactionManager = "primaryTransactionManager")
    public Map<String, Integer> findAllJuecesPenales() {
        List<Persona> jueces = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        int countMujeres = 0;
        int countHombres = 0;
        List<String> roles = List.of("JUEZ");
        List<String> ids = usuarioService.findAllByRoles(roles);
        for (String id : ids) {
            Optional<Persona> juez = personaRepository.findByUsuarioUUID(List.of("PENAL"), id);
            juez.ifPresent(jueces::add);
        }
        for(Persona persona: jueces) {
            if(persona.getSexo().equals(Sexo.FEMENINO))
                countMujeres += 1;
            if(persona.getSexo().equals(Sexo.MASCULINO))
                countHombres += 1;

        }
        map.put("total", jueces.size());
        map.put("hombres", countHombres);
        map.put("mujeres", countMujeres);
        return map;
    }

    @Transactional(transactionManager = "primaryTransactionManager")
    public List<JuezRecord> findByOficialiaOfPersonaLogueada(Integer materiaId) {
        Persona persona = getAuditor();

        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new NotFoundException("Acuerdo rubro no encontrado", "acuerdoRubroId" + materiaId));

        return persona.getOficialia() != null && persona.getOficialia().getJuzgados() != null
                ? persona.getOficialia().getJuzgados().stream()
                        .filter(juzgado -> juzgado.getMateria() != null
                                && materia.getNombre().equals(juzgado.getMateria().getNombre()))
                        .flatMap(juzgado -> findAllJueces(juzgado.getId()).stream())
                        .toList()
                : Collections.emptyList();
    }

    @Transactional(transactionManager = "primaryTransactionManager")
    public List<EncargadoCarritoRecord> findAllEncargadosCarrito() {
        List<String> ids = usuarioService.findAllByRoles(List.of("ENCARGADO_CARRITO"));

        return ids.stream()
                .map(personaRepository::findByUsuario)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(p -> {
                    String name = p.getNombre() + " " + p.getApellidoPaterno();
                    if (p.getApellidoMaterno() != null) {
                        name += " " + p.getApellidoMaterno();
                    }
                    return new EncargadoCarritoRecord(p.getId(), name);
                })
                .toList();
    }

    @Transactional(transactionManager = "primaryTransactionManager")
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
                centrosTrabajo.add(new CentroTrabajoRecord(oficialia.getId(), oficialia.getNombre(),
                        TipoCentroTrabajo.OFICIALIA_COMUN));
            }
        } else {
            centrosTrabajo.add(new CentroTrabajoRecord(currentUser.getJuzgado().getId(),
                    currentUser.getJuzgado().getNombre(), TipoCentroTrabajo.JUZGADO));
        }

        return centrosTrabajo;
    }

    @Transactional(transactionManager = "primaryTransactionManager")
    public Page<PersonaRecordResponse> findAllByCentroTrabajo(String nombre, String searchQuery, Pageable pageable) {
        Persona usuario = getAuditor();
        boolean adminSistema = roleService.hasRole(usuario.getUsuario(), "ADMINISTRADOR_SISTEMA");
        Page<Persona> page = personaRepository.findByCentroTrabajoAndSearch(
                searchQuery,
                usuario.getOficialia() != null ? usuario.getOficialia().getId() : null,
                usuario.getJuzgado() != null ? usuario.getJuzgado().getId() : null,
                adminSistema,
                pageable);

        List<PersonaRecordResponse> list = page.stream()
                .filter(p -> p.getNombre().contains(nombre == null ? "" : nombre))
                .map(p -> new PersonaRecordResponse(
                        p.getId(),
                        p.getNombre() + " " + p.getApellidoPaterno()
                                + (p.getApellidoMaterno() == null ? "" : " " + p.getApellidoMaterno()),
                        p.getCorreoElectronico(),
                        p.getCelular(),
                        (p.getJuzgado() != null) ? p.getJuzgado().getNombre()
                                : (p.getOficialia() != null) ? p.getOficialia().getNombre() : "-",
                        p.getEstado().name(),
                        ""))
                .toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(transactionManager = "primaryTransactionManager")
    public Persona getAuditor() {
        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();
        return personaRepository.findByUsuario(jwt.getSubject())
                .orElseThrow(() -> new NotFoundException(PERSON_NOT_FOUND, "usuario: " + jwt.getSubject()));
    }

    @Transactional(transactionManager = "primaryTransactionManager")
    public List<PersonaRecordResponse> getPersonalTurnado() {
        Persona persona = getAuditor();
        List<Persona> list = new ArrayList<>();
        Integer juzgadoId = persona.getJuzgado() != null ? persona.getJuzgado().getId() : null;

        List<Persona> personasDelJuzgado = personaRepository.findByJuzgadoId(juzgadoId);

        for (Persona item : personasDelJuzgado) {
            if (roleService.hasRole(item.getUsuario(), "ADMINISTRADOR_JUZGADO") ||
                    roleService.hasRole(item.getUsuario(), "AUXILIAR_OFICIAL_MAYOR_JUZGADO") ||
                    persona.getUsuario().equals(item.getUsuario()) ||
                    roleService.getRolesByUserId(item.getUsuario()).isEmpty()) {
                list.add(item);
            }
        }

        if (!list.isEmpty())
            personasDelJuzgado.removeAll(list);

        return personasDelJuzgado.stream().map(p -> new PersonaRecordResponse(
                p.getId(),
                p.getNombre() + " " + p.getApellidoPaterno()
                        + (p.getApellidoMaterno() != null ? " " + p.getApellidoMaterno() : ""),
                p.getCorreoElectronico(),
                p.getCelular(),
                "",
                "",
                roleService.getRolesByUserId(p.getUsuario()).get(0).name())).toList();
    }

    private void validateAdminRole(List<String> rolesToSave, Persona persona) {
        boolean hasAdminRole = rolesToSave.stream().anyMatch(r -> r.equalsIgnoreCase("ADMINISTRADOR_SISTEMA"));
        if (hasAdminRole && ((persona.getJuzgado() != null && persona.getJuzgado().getId() != null)
                || (persona.getOficialia() != null && persona.getOficialia().getId() != null))) {
            throw new ConflictException("Un Administrador (sistema) no puede pertenecer a un centro de trabajo");
        }
        if (hasAdminRole && rolesToSave.size() > 1) {
            throw new ConflictException("Un Administrador (sistema) no puede tener más roles asociados");
        }

        if (!hasAdminRole && ((persona.getJuzgado() == null || persona.getJuzgado().getId() == null)
                && (persona.getOficialia() == null || persona.getOficialia().getId() == null))
                && !rolesToSave.isEmpty()) {
            throw new ConflictException("Seleccione un centro de trabajo para asignar los roles correspondientes");
        }
    }

    private String setRolPrincipal(List<RoleRecord> roles, String rolPrincipal) {
        if (roles.isEmpty()) {
            return "-";
        }
        if (roles.size() == 1) {
            return roles.get(0).name();
        } else {
            return roles.stream().filter(rol -> rol.id().equals(rolPrincipal)).findFirst().get().name();
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

    public List<PersonaRecordResponse> findAllMensajeros() {
        List<String> roles = List.of("MENSAJERO");
        List<String> ids = usuarioService.findAllByRoles(roles);
        List<PersonaRecordResponse> mensajeros = new ArrayList<>();
        for (String id : ids) {
            Optional<Persona> personaOptional = personaRepository.findByUsuario(id);

            if (personaOptional.isPresent()) {
                Persona persona = personaOptional.get();
                if (persona.getEstado().equals(Estado.ACTIVE)) {
                    String name = persona.getNombre() + " " + persona.getApellidoPaterno();
                    name += ((persona.getApellidoPaterno() != null) ? " " + persona.getApellidoMaterno() : "");
                    mensajeros.add(new PersonaRecordResponse(persona.getId(), name, persona.getCorreoElectronico(),
                            persona.getCelular(), "", "", ""));
                }

            }
        }

        return mensajeros;
    }

    public List<CentroTrabajoRecord> findCentroTrabajoByPersonCurrent() {
        Persona persona = getAuditor();
        List<CentroTrabajoRecord> centrosTrabajo = new ArrayList<>();
        if (persona.getJuzgado() != null) {
            centrosTrabajo.add(new CentroTrabajoRecord(persona.getJuzgado().getId(), persona.getJuzgado().getNombre(),
                    TipoCentroTrabajo.JUZGADO));
        }
        if (persona.getOficialia() != null) {
            centrosTrabajo
                    .add(new CentroTrabajoRecord(persona.getOficialia().getId(), persona.getOficialia().getNombre(),
                            TipoCentroTrabajo.OFICIALIA_COMUN));
        }

        return centrosTrabajo;
    }

    public boolean findByEmail(String email) {
        Persona persona = this.personaRepository.findByCorreoElectronico(email);
        return persona != null;
    }

    public void createLitigante(Persona persona, List<RoleRecord> roles) {
        if (!findByEmail(persona.getCorreoElectronico())) {
            List<String> rolesToSave = getNames(roles);
            persona.setUsuario(usuarioService.create(persona));
            persona.setIsExternalUser(ExternalUser.YES);
            roleService.addRoles(persona.getUsuario(), rolesToSave);

            personaRepository.save(persona);
        }
    }

    public List<RoleRecord> getRolesByUser(String userId) {
        return roleService.getRolesByUserId(userId);
    }

    public  ApiResponse<Void> changePassword(CambioPasswordRecord request) {
        String current = request.currentPassword();
        String nueva = request.newPassword();
        String confirmar = request.confirmPassword();
        Persona userLogueado = getAuditor();

        if (!nueva.equals(confirmar)) {
            return ApiResponseFactory.error("Las contraseñas nuevas no coinciden.",
                    ApiResponseFactory.VALIDATION_ERROR);
        }

        if (!validarCredencialesActuales(userLogueado, current)) {
            return ApiResponseFactory.error("La contraseña actual es incorrecta.", ApiResponseFactory.UNAUTHORIZED,
                    401);
        }

        try {
            cambiarPasswordInKeyCloak(userLogueado, nueva);
            return ApiResponseFactory.success("Contraseña actualizada correctamente.");
        } catch (Exception e) {
            return ApiResponseFactory.error("Error al actualizar la contraseña", ApiResponseFactory.INTERNAL_ERROR,
                    500);
        }

    }

    public boolean cambiarPasswordInKeyCloak(Persona user, String password) {

        Keycloak keycloak = keycloakSecurityUtil.getKeycloakInstance();

        UserResource userRepresentation = keycloak.realm(realm).users().get(user.getUsuario());
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);
        cred.setTemporary(false);

        userRepresentation.resetPassword(cred);
        return true;
    }

    public boolean validarCredencialesActuales(Persona user, String currentPassword) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Keycloak keycloak = keycloakSecurityUtil.getKeycloakInstance();
            UserResource userRepresentation = keycloak.realm(realm).users().get(user.getUsuario());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
            map.add("grant_type", "password");
            map.add("username", userRepresentation.getUserSessions().get(0).getUsername());
            map.add("password", currentPassword);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    serverUrlKc,
                    HttpMethod.POST,
                    entity,
                    String.class);

            return response.getStatusCode().is2xxSuccessful();

        } catch (HttpClientErrorException e) {
            log.warn("Credenciales inválidas: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return false;

        } catch (Exception e) {
            log.error("Error al validar credenciales actuales", e);
            return false;
        }
    }

    public String getNamePersona(String usuario) {
        Optional<Persona> persona = personaRepository.findByUsuario(usuario);

        if (persona.isPresent()) {
            Persona p = persona.get();
            return (p.getNombre() + ' ' + p.getApellidoPaterno() + ' '
                    + (p.getApellidoMaterno() != null ? p.getApellidoMaterno() : "")).toUpperCase();
        }
        return "";
    }

    public Persona getOficialMayor(Juzgado juzgado) {
        Keycloak keycloak = keycloakSecurityUtil.getKeycloakInstance();
        String idOficialMayor = keycloak.realm(realm)
                .roles()
                .get("OFICIAL_MAYOR_JUZGADO")
                .getUserMembers()
                .get(0)
                .getId();

        return personaRepository
                .findByUsuarioAndJuzgado(idOficialMayor, juzgado)
                .orElse(null);
    }
}