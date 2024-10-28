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
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.TipoCentroTrabajo;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                        else centroTrabajo = "";
                    }
                    return new PersonaRecordResponse(
                            persona.getId(),
                            persona.getNombre() + " " + persona.getApellidoPaterno() + " " + persona.getApellidoMaterno(),
                            persona.getCorreoElectronico(),
                            persona.getCelular(),
                            centroTrabajo
                    );
                })
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public PersonaRecord findById(Long id) {
        PersonaRecord persona = personaRepository.findByIdAndEstadoIn(id, Arrays.asList(Estado.INACTIVE, Estado.ACTIVE))
                .orElseThrow(() -> new NotFoundException("Persona no encontrada", "personaId"));
        List<RoleRecord> roles = roleService.getRolesByUserId(persona.usuario());
        return persona.withRoles(roles);
    }

    public PersonaRecordResponse create(Persona persona, List<RoleRecord> roles) {
        persona.setUsuario(usuarioService.create(persona));
        roleService.addRoles(persona.getUsuario(), getNames(roles));
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

        persona = personaRepository.save(persona);
        return new PersonaRecordResponse(persona.getId(), persona.getNombre(), persona.getCorreoElectronico(), persona.getCelular(), "");
    }

    public PersonaRecordResponse update(Persona persona, List<RoleRecord> roles) {
        try {
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

            persona = personaRepository.save(persona);
            roleService.updateRoles(persona.getUsuario(), getNames(roles));
            return new PersonaRecordResponse(persona.getId(), persona.getNombre(), persona.getCorreoElectronico(), persona.getCelular(), "");
        } catch (OptimisticLockingFailureException ex) {
            throw new InvalidVersionException(Persona.class.getSimpleName());
        }
    }

    @Transactional(readOnly = true)
    public PersonaRecord findByCurp(String curp) {
        PersonaRecord persona = personaRepository.findByCurp(curp)
                .orElseThrow(() -> new NotFoundException("Persona no encontrada", "curp"));
        List<RoleRecord> roles = roleService.getRolesByUserId(persona.usuario());
        return persona.withRoles(roles);
    }

    private List<String> getNames(List<RoleRecord> list) {
        List<String> roles = new ArrayList<>();
        list.forEach(roleRecord -> roles.add(roleRecord.name()));
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
    public Page<CentroTrabajoRecord> findAllCentroTrabajo(Pageable pageable, String nombre) {
        List<CentroTrabajoRecord> centrosTrabajo = new ArrayList<>();

        List<JuzgadoRecordItem> juzgados = juzgadoRepository.findAllByEstadoIn(List.of(Estado.ACTIVE));
        List<Oficialia> oficialias = oficialiaRepository.findOficialiaComun();

        if (nombre != null && !nombre.isEmpty()) {
            juzgados = juzgados.stream()
                    .filter(juzgado -> juzgado.nombre().toLowerCase().contains(nombre.toLowerCase()))
                    .toList();
            oficialias = oficialias.stream()
                    .filter(oficialia -> oficialia.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                    .toList();
        }

        for (JuzgadoRecordItem juzgado : juzgados) {
            centrosTrabajo.add(new CentroTrabajoRecord(juzgado.id(), juzgado.nombre(), TipoCentroTrabajo.JUZGADO));
        }

        for (Oficialia oficialia : oficialias) {
            centrosTrabajo.add(new CentroTrabajoRecord(oficialia.getId(), oficialia.getNombre(), TipoCentroTrabajo.OFICIALIA_COMUN));
        }

        int totalElements = centrosTrabajo.size();
        int start = (int) pageable.getOffset();
        int end = Math.max(start + pageable.getPageSize(), totalElements);
        List<CentroTrabajoRecord> paginatedList = centrosTrabajo.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, totalElements);
    }

    @Transactional(readOnly = true)
    public Persona getAuditor() {
        Jwt jwt = auditorAware.getCurrentAuditor().orElseThrow();
        return personaRepository.findByUsuario(jwt.getSubject()).orElseThrow(() -> new NotFoundException("Persona no encontrada", "usuaerio: " + jwt.getSubject()));
    }
}