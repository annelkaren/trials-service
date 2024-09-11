package mx.gob.pjpuebla.trials.core.personas;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioService;
import mx.gob.pjpuebla.trials.core.escolaridades.EscolaridadRepository;
import mx.gob.pjpuebla.trials.core.estadocivil.EstadoCivilRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
import mx.gob.pjpuebla.trials.core.usuarios.UsuarioService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final DomicilioService domicilioService;
    private final EscolaridadRepository escolaridadRepository;
    private final EstadoCivilRepository estadoCivilRepository;
    private final JuzgadoRepository juzgadoRepository;
    private final UsuarioService usuarioService;
    private final RoleService roleService;

    @Transactional(readOnly = true)
    public Page<PersonaRecordResponse> getAll(Persona example, Pageable pageable) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<Persona> page = personaRepository.findAll(Example.of(example, exampleMatcher), pageable);

        List<PersonaRecordResponse> list = page.getContent().stream()
                .map(persona ->
                        new PersonaRecordResponse(persona.getId(),
                                persona.getNombre() + " " + persona.getApellidoPaterno() + " " + persona.getApellidoMaterno(),
                                persona.getCorreoElectronico(),
                                persona.getCelular()))
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
        persona.setJuzgado(juzgadoRepository.findById(persona.getJuzgado().getId())
                .orElseThrow(() -> new NotFoundException("Juzgado no encontrado", "juzgadoId")));
        persona.setDomicilio(domicilioService.save(persona.getDomicilio()));
        persona = personaRepository.save(persona);
        return new PersonaRecordResponse(persona.getId(), persona.getNombre(), persona.getCorreoElectronico(), persona.getCelular());
    }

    public PersonaRecordResponse update(Persona persona, List<RoleRecord> roles) {
        try {
            persona.setEscolaridad(escolaridadRepository.findById(persona.getEscolaridad().getId())
                    .orElseThrow(() -> new NotFoundException("Escolaridad no encontrada", "escolaridadId")));
            persona.setEstadoCivil(estadoCivilRepository.findById(persona.getEstadoCivil().getId())
                    .orElseThrow(() -> new NotFoundException("Estado Civil no encontrado", "estadoCivilId")));
            persona.setJuzgado(juzgadoRepository.findById(persona.getJuzgado().getId())
                    .orElseThrow(() -> new NotFoundException("Juzgado no encontrado", "juzgadoId")));
            persona.setDomicilio(domicilioService.save(persona.getDomicilio()));
            persona = personaRepository.save(persona);
            roleService.updateRoles(persona.getUsuario(), getNames(roles));
            return new PersonaRecordResponse(persona.getId(), persona.getNombre(), persona.getCorreoElectronico(), persona.getCelular());
        } catch (OptimisticLockingFailureException ex) {
            log.error("update -> {}", ex);
            throw new mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException("Persona modificada por otro usuario", "personaId");
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
        list.stream().forEach(roleRecord -> roles.add(roleRecord.name()));
        return roles;
    }

    @Transactional(readOnly = true) 
    List<PersonaSalaRecord> findAllJueces(){
        return personaRepository.findAllJueces();
    }
}