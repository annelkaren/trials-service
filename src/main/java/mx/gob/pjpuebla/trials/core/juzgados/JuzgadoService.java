package mx.gob.pjpuebla.trials.core.juzgados;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.oficialias.OficialiaJuzgadoRecord;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRecord;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.error.ConstraintViolationException;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.util.Messages;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.InstanciaJuzgado;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFolios;
import mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFoliosRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoItemRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class JuzgadoService {

    private static final String JUZGADO_NOT_FOUND = "Juzgado no encontrado";
    private final JuzgadoRepository juzgadoRepository;
    private final TipoJuicioRepository tipoJuicioRepository;
    private final SedeRepository sedeRepository;
    private final MateriaRepository materiaRepository;
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final TipoPartesRepository tipoPartesRepository;
    private final JuzgadoFoliosRepository juzgadoFoliosRepository;
    private final PersonaService personaService;

    private static final Random RANDOM = new Random();

    @Transactional(readOnly = true)
    public Page<JuzgadoRecordItem> getAll(String key, Pageable pageable) {
        String finalKey = (key != null) ? StringUtils.stripAccents(key).toLowerCase() : "";

        Page<Juzgado> page = juzgadoRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
        List<JuzgadoRecordItem> list = page.getContent().stream()
                .filter(juzgado -> finalKey.isEmpty() || StringUtils.stripAccents(juzgado.getNombre())
                        .toLowerCase()
                        .contains(finalKey))
                .map(juzgado -> new JuzgadoRecordItem(
                        juzgado.getId(),
                        juzgado.getNombre(),
                        juzgado.getEstado(),
                        StringUtils.capitalize(StringUtils.stripAccents(juzgado.getMateria().getNombre()).toLowerCase())
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<OficialiaJuzgadoRecord> findByOficialiaId(Integer id) {
        List<Estado> estados = List.of(Estado.ACTIVE);
        List<Juzgado> juzgados = juzgadoRepository.findByOficialiaIdAndEstadoIn(id, estados);

        return juzgados.stream().map(j -> new OficialiaJuzgadoRecord(j.getId(), j.getNombre())).toList();
    }

    @Transactional(readOnly = true)
    public JuzgadoRecord findById(Integer id) {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Juzgado juzgado = juzgadoRepository.findByIdAndEstadoIn(id, estados)
                .orElseThrow(() -> new NotFoundException(JUZGADO_NOT_FOUND, "juzgadoId"));
        List<TipoJuicioRecord> tipoJuicios = juzgado.getTipoJuicios().stream()
                .map(tj -> new TipoJuicioRecord(
                        tj.getId(),
                        tj.getNombre(), null, null
                )).toList();
        if (juzgado.getInstanciaJuzgado() == null) {
            throw new IllegalStateException("InstanciaJuzgado no debe ser null");
        }
        return new JuzgadoRecord(
                juzgado.getId(),
                juzgado.getVersion(),
                juzgado.getNombre(),
                juzgado.getEstado(),
                juzgado.getMateria().getId(),
                juzgado.getSede().getId(),
                juzgado.getMaxAsignacionesRonda(),
                juzgado.getContadorAsignaciones(),
                juzgado.getInstanciaJuzgado().ordinal(),
                tipoJuicios
        );
    }

    public JuzgadoRecordItem create(Juzgado juzgado) {
        if (juzgadoRepository.findByNombreIgnoreCase(juzgado.getNombre()).isPresent()) {
            throw new ConflictException("No pueden existir 2 juzgados con el mismo nombre");
        }

        Materia materia = materiaRepository.findById(juzgado.getMateria().getId()).orElseThrow(() -> new NotFoundException("Materia no encontrada", "materiaId"));
        juzgado.setMateria(materia);

        Sede sede = sedeRepository.findById(juzgado.getSede().getId()).orElseThrow(() -> new NotFoundException("Sede no encontrada", "sedeId"));
        juzgado.setSede(sede);

        juzgado.setContadorAsignaciones(0);
        if (juzgado.getMaxAsignacionesRonda() == null) {
            juzgado.setMaxAsignacionesRonda(2);
        }
        List<Integer> tjIds = juzgado.getTipoJuicios().stream().map(TipoJuicio::getId).toList();
        List<TipoJuicio> tipojuicios = tipoJuicioRepository.findAllById(tjIds);
        juzgado.setTipoJuicios(tipojuicios);

        //Llena JuzgadoFolios para insertar en tabla JUZGADO_FOLIOS: crea folio para exhorto_entrada
        //Añadir otra linea juzgadoFolios.add(); para añadir otro tipo de folio
        List<JuzgadoFolios> juzgadoFolios = new ArrayList<>();
        juzgadoFolios.add(new JuzgadoFolios().setTipoCarpeta(TipoCarpeta.DEMANDA).setJuzgado(juzgado));
        juzgadoFolios.add(new JuzgadoFolios().setTipoCarpeta(TipoCarpeta.EXHORTO).setJuzgado(juzgado));
        juzgadoFolios.add(new JuzgadoFolios().setTipoCarpeta(TipoCarpeta.APELACION).setJuzgado(juzgado));
        juzgadoFolios.add(new JuzgadoFolios().setTipoCarpeta(TipoCarpeta.DESPACHO).setJuzgado(juzgado));
        juzgadoFolios.add(new JuzgadoFolios().setTipoCarpeta(TipoCarpeta.APELACION_MUNICIPAL).setJuzgado(juzgado));
        juzgadoFolios.add(new JuzgadoFolios().setTipoCarpeta(TipoCarpeta.AMPARO).setJuzgado(juzgado));
        juzgado.setJuzgadoFolios(juzgadoFolios);

        juzgado.setInstanciaJuzgado(TipoCarpeta.EXHORTO.name().equalsIgnoreCase(materia.getNombre())
                ? InstanciaJuzgado.NO_APLICA
                : juzgado.getInstanciaJuzgado());

        juzgado = juzgadoRepository.save(juzgado);
        return new JuzgadoRecordItem(
                juzgado.getId(),
                juzgado.getNombre(),
                juzgado.getEstado(),
                juzgado.getMateria().getNombre()
        );
    }

    public JuzgadoRecordItem update(Juzgado juzgado) {
        Optional<Juzgado> test = juzgadoRepository.findByNombreIgnoreCase(juzgado.getNombre());
        if (test.isPresent() && !Objects.equals(test.get().getId(), juzgado.getId())) {
            throw new ConflictException("No pueden existir 2 juzgados con el mismo nombre");
        }
        try {
            juzgado.setMateria(materiaRepository.findById(juzgado.getMateria().getId()).orElseThrow(() -> new NotFoundException("Materia no encontrada", "materiaId")));
            juzgado.setSede(sedeRepository.findById(juzgado.getSede().getId()).orElseThrow(() -> new NotFoundException("Sede no encontrada", "sedeId")));
            Juzgado juzgadoAsignaciones = juzgadoRepository.findById(juzgado.getId()).orElseThrow(() -> new NotFoundException(JUZGADO_NOT_FOUND, "juzgadoId"));

            List<Integer> tjIds = juzgado.getTipoJuicios().stream().map(TipoJuicio::getId).toList();
            List<TipoJuicio> tipojuicios = tipoJuicioRepository.findAllById(tjIds);
            juzgado.setTipoJuicios(tipojuicios);
            juzgado.setContadorAsignaciones(juzgadoAsignaciones.getContadorAsignaciones());
            juzgado.setInstanciaJuzgado(juzgado.getInstanciaJuzgado());
            juzgado = juzgadoRepository.save(juzgado);
            return new JuzgadoRecordItem(
                    juzgado.getId(),
                    juzgado.getNombre(),
                    juzgado.getEstado(),
                    juzgado.getMateria().getNombre()
            );
        } catch (OptimisticLockingFailureException ex) {
            throw new InvalidVersionException(Juzgado.class.getSimpleName());
        }
    }

    public void delete(Integer id) {
        try {
            juzgadoRepository.deleteById(id);
            juzgadoRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConstraintViolationException(Messages.CONSTRAINT_ERROR, "juzgadoId " + id);
        }
    }

    public Juzgado getConexidadJuzgado(PersonaDocumentoItemRecord actor, PersonaDocumentoItemRecord demandado, TipoJuicio tipoJuicio) {
        List<Carpeta> carpetas = new ArrayList<>();
        TipoPartes actorParte = tipoPartesRepository.findByNombreAndTipoJuicioId("Actor", tipoJuicio.getId()).orElseThrow(() -> new NotFoundException("Tipo Parte no encontrado", actor.tipoParte().toString()));
        TipoPartes demandadoParte = tipoPartesRepository.findByNombreAndTipoJuicioId("Demandado", tipoJuicio.getId()).orElseThrow(() -> new NotFoundException("Tipo Parte no encontrado", demandado.tipoParte().toString()));

        List<PersonaDocumento> registrosActor = personaDocumentoRepository
                .findByNombreIgnoreCaseAndApellidoPaternoIgnoreCaseAndApellidoMaternoIgnoreCaseAndPseudonimoIgnoreCaseAndTipoPartesId(
                        actor.nombre(), actor.apellidoPaterno(), actor.apellidoMaterno(), actor.pseudonimo(), actorParte.getId());

        List<PersonaDocumento> registrosDemandado = personaDocumentoRepository
                .findByNombreIgnoreCaseAndApellidoPaternoIgnoreCaseAndApellidoMaternoIgnoreCaseAndPseudonimoIgnoreCaseAndTipoPartesId(
                        demandado.nombre(), demandado.apellidoPaterno(), demandado.apellidoMaterno(), demandado.pseudonimo(), demandadoParte.getId());

        if (registrosActor.isEmpty() || registrosDemandado.isEmpty()) {
            return null;
        }

        for (PersonaDocumento tmp : registrosActor) {
            carpetas.add(tmp.getCarpeta());
        }

        for (PersonaDocumento tmp : registrosDemandado) {
            Carpeta carpeta;

            if (!carpetas.contains(tmp.getCarpeta()))
                continue;

            carpeta = tmp.getCarpeta();

            if (juzgadoRepository.findByMateriaAndEstado(tipoJuicio.getMateria(), Estado.ACTIVE).contains(carpeta.getJuzgado()))
                return carpeta.getJuzgado();
        }
        return null;
    }

    public Juzgado getJuzgado(TipoJuicio tipoJuicio, TipoCarpeta tipoCarpeta, List<Juzgado> juzgadosRelacionados) {
        InstanciaJuzgado instanciaJuzgado;
        String reason;

        if (TipoCarpeta.APELACION.equals(tipoCarpeta)) {
            instanciaJuzgado = InstanciaJuzgado.SEGUNDA_INSTANCIA;
            reason = "No hay sala disponible para asignar.";
        } else if (TipoCarpeta.EXHORTO.equals(tipoCarpeta)) {
            instanciaJuzgado = InstanciaJuzgado.NO_APLICA;
            reason = "No se encontró un Juzgado de la materia " + tipoJuicio.getMateria().getNombre() + " para asignar. ";
        } else {
            instanciaJuzgado = InstanciaJuzgado.PRIMERA_INSTANCIA;
            reason = "No hay juzgados relacionados a la Oficialia";
        }

        if (juzgadosRelacionados.isEmpty()){
            throw new NotFoundException(reason, "juzgadosRelacionados");
        }

        List<Juzgado> juzgados = juzgadoRepository.findJuzgadosMenosAsignaciones(tipoJuicio.getMateria(), instanciaJuzgado,
                juzgadosRelacionados.stream().map(Juzgado::getId).toList());

        if (juzgados.isEmpty()) {
            revisarCargaJuzgados(tipoJuicio.getMateria(), tipoCarpeta, juzgadosRelacionados);
            juzgados = juzgadoRepository.findJuzgadosMenosAsignaciones(tipoJuicio.getMateria(), instanciaJuzgado,
                    juzgadosRelacionados.stream().map(Juzgado::getId).toList());
        
            if (juzgados.isEmpty()){
                if (TipoCarpeta.APELACION.name().equals(tipoCarpeta.name())) {
                    throw new NotFoundException(reason, tipoJuicio.getNombre());
                }
                throw (new NotFoundException(reason, tipoJuicio.getNombre()));
            }

        }

        int rand = RANDOM.nextInt(juzgados.size());

        return juzgados.get(rand);

    }

    public void actualizarCarga(Juzgado juzgado, TipoCarpeta tipoCarpeta, List<Juzgado> juzgadosRelacionados) {
        juzgadoRepository.actualizarContadorAsignaciones(juzgado.getId());
        revisarCargaJuzgados(juzgado.getMateria(), tipoCarpeta, juzgadosRelacionados);
    }

    public void revisarCargaJuzgados(Materia materia, TipoCarpeta tipoCarpeta, List<Juzgado> juzgadosRelacionados) {

        InstanciaJuzgado instanciaJuzgado;
        if (TipoCarpeta.APELACION.equals(tipoCarpeta)) {
            instanciaJuzgado = InstanciaJuzgado.SEGUNDA_INSTANCIA;

        } else if (TipoCarpeta.EXHORTO.equals(tipoCarpeta)) {
            instanciaJuzgado = InstanciaJuzgado.NO_APLICA;
        } else {
            instanciaJuzgado = InstanciaJuzgado.PRIMERA_INSTANCIA;
        }

        List<Integer> idsJuzgadosRelacionados = juzgadosRelacionados.stream().map(juzgado -> juzgado.getId()).toList();

        int totalAsignaciones = juzgadoRepository.sumContadorAsignacionesByMateria(materia, instanciaJuzgado);
        int totalMaxAsignaciones = juzgadoRepository.sumMaxAsignacionesRondaByMateria(materia, instanciaJuzgado);
        int totalJuzgadosMenosAsignaciones = juzgadoRepository.findJuzgadosMenosAsignaciones(materia, instanciaJuzgado, idsJuzgadosRelacionados).size();

        if (totalAsignaciones >= totalMaxAsignaciones && totalJuzgadosMenosAsignaciones == 0) {
            juzgadoRepository.reiniciarContadorAsignaciones(materia, instanciaJuzgado);
        }
    }

    public JuzgadoFolios getJuzgadoFolios(Juzgado juzgado, TipoCarpeta tipoCarpeta) {

        return juzgadoFoliosRepository.findByJuzgadoAndTipoCarpeta(juzgado, tipoCarpeta)
                .orElseThrow(() -> new NotFoundException("juzgadoFolio no encontrado", "tipoCarpeta"));
    }

    public JuzgadoFolios checkYearJuzgadoFolios(JuzgadoFolios juzgadoFolios) {
        Integer currentYear = LocalDate.now().getYear();
        if (juzgadoFolios.getYear() < currentYear) {
            juzgadoFolios.setYear(currentYear);
            juzgadoFolios.setValue(1);
            juzgadoFolios = juzgadoFoliosRepository.save(juzgadoFolios);
        }
        return juzgadoFolios;
    }

    public JuzgadoFolios increaseValueJuzgadoFolios(JuzgadoFolios juzgadoFolios) {
        juzgadoFolios.setValue(juzgadoFolios.getValue() + 1);
        juzgadoFolios = juzgadoFoliosRepository.save(juzgadoFolios);
        return juzgadoFolios;
    }

    @Transactional(readOnly = true)
    public List<JuzgadoRecordItem> findAllByEstadoAutocomplete(String key) {
        key = (key != null) ? key.toLowerCase() : "";
        Persona personaLogueada = personaService.getAuditor();

        String oficialia = personaLogueada.getOficialia() != null ? "Oficialia" : null;
        Integer oficialiaId = personaLogueada.getOficialia() != null ? personaLogueada.getOficialia().getId() : null;
        String centroTrabajo = personaLogueada.getJuzgado() != null ? "Juzgado" : oficialia;
        Integer idCentroTrabajo = personaLogueada.getJuzgado() != null ? personaLogueada.getJuzgado().getId() : oficialiaId;

        return juzgadoRepository.findAllByEstadoAutocomplete(Estado.ACTIVE, key, centroTrabajo, idCentroTrabajo);
    }

    public JuzgadoRecordItem updateStatus(Integer id, Integer status) {
        Estado estado = Estado.values()[status];
        Juzgado juzgado = juzgadoRepository.findById(id).
                orElseThrow(() -> new NotFoundException(JUZGADO_NOT_FOUND, id.toString()));
        juzgado.setEstado(estado);
        juzgadoRepository.save(juzgado);

        return new JuzgadoRecordItem(id, juzgado.getNombre(), juzgado.getEstado(), "");
    }

    public List<JuzgadoRecordItem> findAllByInstancia(InstanciaJuzgado instanciaJuzgado){
        return  juzgadoRepository.findAllByInstancia(instanciaJuzgado);
    }

    public JuzgadoRecordItem getJuzgadoActual() {
        Persona persona = personaService.getAuditor();

        if (persona.getJuzgado() == null) {
            throw new NotFoundException("El usuario actual no esta asignado a un juzgado", "Juzgado");
        }

        Juzgado juzgadoActual = persona.getJuzgado();

        return new JuzgadoRecordItem(juzgadoActual.getId(), juzgadoActual.getNombre(), juzgadoActual.getEstado(), juzgadoActual.getMateria().getNombre());
    }
}