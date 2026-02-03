package mx.gob.pjpuebla.trials.core.salas;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.bloques.BloqueCitaItem;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRepository;
import mx.gob.pjpuebla.trials.core.eventos.EventoService;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.salaPersona.SalaPersona;
import mx.gob.pjpuebla.trials.core.salaPersona.SalaPersonaRepository;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudiencia;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;

import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.audiencias.Audiencia;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class SalaService {

    private final SalaRepository salaRepository;
    private final JuzgadoRepository juzgadoRepository;
    private final BloqueRepository bloqueRepository;
    private final PersonaRepository juezRepository;
    private final AudienciaRepository audienciaRepository;
    private final TipoPartesRepository tipoPartesRepository;
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final EventoService eventoService;
    private final PersonaService personaService;
    private final SalaPersonaRepository salaPersonaRepository;

    private static final Integer TIEMPO_ESPERA_AUDIENCIA = 3;

    @Transactional(readOnly = true)
    public Page<SalaRecord> getAll(Sala example, Pageable pageable) {

        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<Sala> page = salaRepository.findAll(Example.of(example, exampleMatcher), pageable);
        Persona persona = personaService.getAuditor();

        List<SalaRecord> list = page.getContent().stream()
                .filter(sala -> {
                    return sala.getJuzgado().equals(persona.getJuzgado());
                })
                .map(sala -> new SalaRecord(
                        sala.getId(),
                        sala.getNombre(),
                        getNameJuez(sala),
                        sala.getJuzgado().getNombre(),
                        new mx.gob.pjpuebla.trials.core.bloques.BloqueRecord(sala.getBloque().getId(),
                                sala.getBloque().getHoraInicial(), sala.getBloque().getHoraFinal()),
                        sala.getEstado()))

                .toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());

    }

    private String getNameJuez(Sala sala) {
        Persona juez = sala.getJuez();
        if (juez != null) {
            return juez.getNombre() + " " + juez.getApellidoPaterno() + " "
                    + ((juez.getApellidoMaterno() != null) ? juez.getApellidoMaterno() : "");
        }
        return "Por asignar";
    }

    public List<SalaRecord> getAllByJuzgado() {
        // Traemos las salas relacionadas al juzgado de la persona logueda.
        Integer juzgadoId = personaService.getAuditor().getJuzgado().getId();

        return salaRepository.findByJuzgado(juzgadoId);
    }

    @Transactional(readOnly = true)
    public SalaRecordResponse findById(Integer id) {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);

        return salaRepository.findByIdAndEstadoIn(id, estados)
                .map(base -> {
                    List<SecretariosSalasRecord> secretarios = salaPersonaRepository.getSecretariosFromSala(id);
                    return new SalaRecordResponse(
                            base.id(), base.nombre(), base.estado(), base.version(),
                            base.juez(), base.juzgado(), base.bloque(),
                            secretarios);

                })
                .orElseThrow(() -> new NotFoundException("Sala no encontrada", "salaId"));
    }

    public Integer create(SalaRecordSave salaRecord) {

        Sala sala = new Sala();

        // Validar juez no asignado a otra sala (distinta a esta)
        if (salaRecord.juezId() != null) {
            validaJuezAsignadoAOtraSala(salaRecord.juezId(), null);
        }

        sala = construirSala(salaRecord, sala);
        sala = salaRepository.save(sala);

        if (salaRecord.secretarios() != null && !salaRecord.secretarios().isEmpty()) {
            actualizarSecretariosEnSala(sala.getId(), salaRecord.secretarios());
        }

        return sala.getId();
    }

    private void validaJuezAsignadoAOtraSala(Integer juezId, Integer salaIdActual) {
        salaRepository.findByJuezId(juezId)
                .ifPresent(salaConEseJuez -> {
                    boolean esOtraSala = (salaIdActual == null) || !salaConEseJuez.getId().equals(salaIdActual);
                    if (esOtraSala) {
                        throw new ConflictException(
                                "El juez seleccionado tiene una sala asignada, por favor eliga otro.");
                    }
                });
    }

    private Sala construirSala(SalaRecordSave salaRecord, Sala sala) {
        Persona juez = salaRecord.juezId() != null
                ? juezRepository.findById(salaRecord.juezId().longValue()).orElse(null)
                : null;

        Bloque bloque = bloqueRepository.findById(salaRecord.bloqueId())
                .orElseThrow(() -> new NotFoundException("Bloque no encontrado", "bloqueId: " + salaRecord.bloqueId()));

        Juzgado juzgado = juzgadoRepository.findById(salaRecord.juzgadoId())
                .orElseThrow(
                        () -> new NotFoundException("Juzgado no encontrado", "juzgadoId: " + salaRecord.juzgadoId()));

        sala.setJuez(juez);
        sala.setBloque(bloque);
        sala.setJuzgado(juzgado);
        sala.setEstado(salaRecord.estado());
        if (sala.getId() == null) {
            sala.setNombre(getNameOfSala(sala));
        }

        return sala;
    }

    @Transactional
    public Integer update(SalaRecordSave salaRecord) {
        try {
            Sala sala = salaRepository.findById(salaRecord.salaId())
                    .orElseThrow(() -> new NotFoundException("Sala no encontrada", "salaId: " + salaRecord.salaId()));

            // Validar juez no asignado a otra sala (distinta a esta)
            if (salaRecord.juezId() != null) {
                validaJuezAsignadoAOtraSala(salaRecord.juezId(), sala.getId());
            }

            sala = construirSala(salaRecord, sala);

            actualizarSecretariosEnSala(sala.getId(), salaRecord.secretarios());

            salaRepository.save(sala);
            return sala.getId();

        } catch (org.springframework.dao.OptimisticLockingFailureException ex) {
            throw new InvalidVersionException(Sala.class.getSimpleName());
        }
    }

    @Transactional
    private void actualizarSecretariosEnSala(Integer salaId, List<Long> secretarios) {

        // 0) null-safe + dedupe (Set)
        Set<Long> incomingIds = (secretarios == null) ? Set.of() : new HashSet<>(secretarios);

        // 1) cargar existentes de esa sala (idealmente JOIN FETCH persona)
        List<SalaPersona> existentes = salaPersonaRepository.findAllBySalaIdWithPersona(salaId);

        // 2) indexar por personaId para búsquedas O(1)
        Map<Long, SalaPersona> porPersonaId = new HashMap<>(existentes.size() * 2);
        for (SalaPersona sp : existentes) {
            porPersonaId.put(sp.getPersona().getId(), sp);
        }

        // 3) precargar datos necesarios (rolPrincipal) en UNA sola consulta
        Map<Long, String> rolPorPersonaId = incomingIds.isEmpty()
                ? Map.of()
                : juezRepository.findAllById(incomingIds).stream()
                        .collect(java.util.stream.Collectors.toMap(
                                Persona::getId,
                                Persona::getRolPrincipal));

        if (rolPorPersonaId.size() != incomingIds.size()) {
            Set<Long> faltantes = new HashSet<>(incomingIds);
            faltantes.removeAll(rolPorPersonaId.keySet());
            throw new IllegalArgumentException("Personas inexistentes: " + faltantes);
        }

        // 4) activar o insertar los que vienen
        Sala salaRef = salaRepository.getReferenceById(salaId);

        for (Long personaId : incomingIds) {
            SalaPersona sp = porPersonaId.get(personaId);

            if (sp != null) {
                // ya existía → activar
                sp.setEstado(Estado.ACTIVE);

                // si quieres asegurar rol actualizado (por si cambió el rolPrincipal)
                sp.setRol(rolPorPersonaId.get(personaId));
            } else {
                // no existía → insertar nueva relación
                SalaPersona nuevo = new SalaPersona();

                // proxy barato (sin SELECT)
                Persona personaRef = juezRepository.getReferenceById(personaId);

                nuevo.setSala(salaRef);
                nuevo.setPersona(personaRef);

                // rol tomado del map precargado (NO del proxy)
                nuevo.setRol(rolPorPersonaId.get(personaId));

                nuevo.setEstado(Estado.ACTIVE);

                existentes.add(nuevo);
                porPersonaId.put(personaId, nuevo);
            }
        }

        // 5) desactivar los existentes que NO vienen en la lista
        for (SalaPersona sp : existentes) {
            Long personaId = sp.getPersona().getId();
            if (!incomingIds.contains(personaId)) {
                sp.setEstado(Estado.INACTIVE);
            }
        }

        // 6) persistir todo en un solo flush/commit (por estar en @Transactional)
        salaPersonaRepository.saveAll(existentes);
    }

    public String getNameOfSala(Sala sala) {
        if (sala.getJuzgado() != null && sala.getJuzgado().getId() != null) {
            int juzgadoId = sala.getJuzgado().getId();

            long count = salaRepository.countByJuzgadoId(juzgadoId);

            return String.valueOf(count + 1);
        } else {

            return "1";
        }
    }

    /***
     * 
     * @param juzgado       Juzgado del Distrito
     * @param tipoAudiencia Tipo de Audiencia de Oralidad
     * @return SalaAudienciaRecord Primera sala disponible del Juzgado según la
     *         configuración del bloque
     */
    public SalaAudienciaRecord asignarSala(Juzgado juzgado, TipoAudiencia tipoAudiencia) {
        LocalDate fecha = LocalDate.now().plusDays(TIEMPO_ESPERA_AUDIENCIA);
        LocalDateTime ultimaFechaAudiencia = audienciaRepository.getFechaUltimaAudiencia(juzgado, tipoAudiencia);
        List<Bloque> bloques = bloqueRepository.findBloquesSalasJuzgado(juzgado);

        if (ultimaFechaAudiencia != null && ultimaFechaAudiencia.toLocalDate().isAfter(fecha)) {
            fecha = ultimaFechaAudiencia.toLocalDate();
        }

        int max = 3;
        int intentos = 0;

        while (intentos <= max) {

            if (eventoService.esDiaInHabil(fecha, juzgado, null) == Boolean.TRUE) {
                fecha = eventoService.siguienteDiaHabil(fecha, juzgado, null);
            }

            for (Bloque bloque : bloques) {
                List<BloqueCitaItem> citas = bloque.getData().getCitas();

                for (BloqueCitaItem cita : citas) {
                    LocalDateTime fechaHoraAudiencia = LocalDateTime.of(fecha, cita.getHoraCitas());

                    Sala salaDisponible = this.findSalaDisponible(fechaHoraAudiencia, bloque, juzgado);

                    if (salaDisponible != null) {
                        return new SalaAudienciaRecord(salaDisponible.getId(), salaDisponible.getNombre(),
                                salaDisponible.getJuez().getId(), salaDisponible.getJuez().getNombre(),
                                juzgado.getNombre(),
                                bloque.getId(), fechaHoraAudiencia);
                    }
                }
            }

            fecha = fecha.plusDays(1);
            intentos++;
        }

        throw new NotFoundException("No existe una sala disponible", "Sala");
    }

    public Sala findSalaDisponible(LocalDateTime fechaAudiencia, Bloque bloque, Juzgado juzgado) {
        Optional<Sala> salaDisponible = salaRepository.findSalaDisponible(fechaAudiencia, bloque, juzgado)
                .stream().findFirst();

        return salaDisponible.orElse(null);
    }

    public SalaAudienciaRecord asignarSalaConexidad(PersonaDocumentoRecord actor, PersonaDocumentoRecord demandado,
            TipoJuicio tipoJuicio, TipoAudiencia tipoAudiencia) {
        List<Carpeta> carpetas = new ArrayList<>();
        String nombreMateria = tipoJuicio.getMateria().getNombre();

        TipoPartes actorParte = tipoPartesRepository
                .findByNombreAndTipoJuicioId(
                        nombreMateria.equals("PENAL") || nombreMateria.equals("JUSTICIA PARA ADOLESCENTES") ? "Victimas"
                                : "Actor",
                        tipoJuicio.getId())
                .orElseThrow(() -> new NotFoundException("Tipo Parte no encontrado", actor.tipoParte()));
        TipoPartes demandadoParte = tipoPartesRepository.findByNombreAndTipoJuicioId(
                nombreMateria.equals("PENAL") || nombreMateria.equals("JUSTICIA PARA ADOLESCENTES") ? "Imputados"
                        : "Demandado",
                tipoJuicio.getId())
                .orElseThrow(() -> new NotFoundException("Tipo Parte no encontrado", demandado.tipoParte()));

        List<PersonaDocumento> registrosActor = personaDocumentoRepository
                .findByNombreIgnoreCaseAndApellidoPaternoIgnoreCaseAndApellidoMaternoIgnoreCaseAndPseudonimoIgnoreCaseAndTipoPartesId(
                        actor.nombre(), actor.apellidoPaterno(), actor.apellidoMaterno(), actor.pseudonimo(),
                        actorParte.getId());

        List<PersonaDocumento> registrosDemandado = personaDocumentoRepository
                .findByNombreIgnoreCaseAndApellidoPaternoIgnoreCaseAndApellidoMaternoIgnoreCaseAndPseudonimoIgnoreCaseAndTipoPartesId(
                        demandado.nombre(), demandado.apellidoPaterno(), demandado.apellidoMaterno(),
                        demandado.pseudonimo(), demandadoParte.getId());

        if (registrosActor.isEmpty() || registrosDemandado.isEmpty()) {
            return null;
        }

        for (PersonaDocumento tmp : registrosActor) {
            carpetas.add(tmp.getCarpeta());
        }

        for (PersonaDocumento tmp : registrosDemandado) {
            Optional<Audiencia> audiencia;

            if (!carpetas.contains(tmp.getCarpeta()))
                continue;

            audiencia = audienciaRepository.findByCarpeta(tmp.getCarpeta());

            if (audiencia.isPresent()) {
                return asignarAudiencia(audiencia.get().getSala(), tipoAudiencia);
            }
        }

        return null;
    }

    /***
     * 
     * @param sala          Sala de Audiencia
     * @param tipoAudiencia Tipo de Audiencia de Oralidad
     * @return SalaAudienciaRecord Primer horario disponible de la Sala
     */
    public SalaAudienciaRecord asignarAudiencia(Sala sala, TipoAudiencia tipoAudiencia) {
        LocalDate fecha = LocalDate.now().plusDays(TIEMPO_ESPERA_AUDIENCIA);
        LocalDateTime ultimaFechaAudiencia = audienciaRepository.getFechaUltimaAudiencia(sala.getJuzgado(),
                tipoAudiencia);
        Bloque bloque = sala.getBloque();
        int max = 3;
        int intentos = 0;

        if (ultimaFechaAudiencia != null && ultimaFechaAudiencia.toLocalDate().isAfter(fecha)) {
            fecha = ultimaFechaAudiencia.toLocalDate();
        }

        List<BloqueCitaItem> citas = bloque.getData().getCitas();

        while (intentos <= max) {

            if (eventoService.esDiaInHabil(fecha, sala.getJuzgado(), null) == Boolean.TRUE) {
                fecha = eventoService.siguienteDiaHabil(fecha, sala.getJuzgado(), null);
            }

            for (BloqueCitaItem cita : citas) {
                LocalDateTime fechaHoraAudiencia = LocalDateTime.of(fecha, cita.getHoraCitas());

                if (checkHoraDisponible(fechaHoraAudiencia, sala) == Boolean.TRUE) {
                    return new SalaAudienciaRecord(sala.getId(), sala.getNombre(),
                            sala.getJuez().getId(), sala.getJuez().getNombre(), sala.getJuzgado().getNombre(),
                            bloque.getId(), fechaHoraAudiencia);
                }
            }

            fecha = fecha.plusDays(1);
            intentos++;
        }

        throw new NotFoundException("No hay Horario disponible", "Sala");

    }

    public Boolean checkHoraDisponible(LocalDateTime fechaAudiencia, Sala sala) {
        Optional<Sala> salaDisponible = salaRepository.checkHoraDisponible(fechaAudiencia, sala);

        if (salaDisponible.isPresent())
            return Boolean.TRUE;

        return Boolean.FALSE;
    }

    @Transactional(readOnly = true)
    public List<SalaRecord> getSalasByJuzgado(String nombre, Integer idAudiencia) {

        Audiencia audiencia = audienciaRepository.findById(idAudiencia)
                .orElseThrow(() -> new NotFoundException("Audiencia no encontrada", "AudienciaId"));

        Juzgado juzgado = audiencia.getCarpeta().getJuzgado();

        List<Sala> salas = salaRepository.findByJuzgadoAndNombreContainingIgnoreCase(juzgado, nombre);

        return salas.stream()
                .map(sala -> new SalaRecord(
                        sala.getId(),
                        sala.getNombre(),
                        sala.getJuez().getNombre() + " " + sala.getJuez().getApellidoPaterno() + " "
                                + ((sala.getJuez().getApellidoMaterno() != null) ? sala.getJuez().getApellidoMaterno()
                                        : ""),
                        sala.getJuzgado().getNombre(),
                        new mx.gob.pjpuebla.trials.core.bloques.BloqueRecord(sala.getBloque().getId(),
                                sala.getBloque().getHoraInicial(), sala.getBloque().getHoraFinal()),
                        sala.getEstado()))
                .toList();
    }

}
