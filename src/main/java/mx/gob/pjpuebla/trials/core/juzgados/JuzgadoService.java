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
import mx.gob.pjpuebla.trials.workflow.contadoresJuzgados.ContadorJuzgado;
import mx.gob.pjpuebla.trials.workflow.contadoresJuzgados.ContadorJuzgadoRepository;
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
    private final ContadorJuzgadoRepository contadorJuzgadoRepository;

    private static final Random RANDOM = new Random();

    @Transactional(readOnly = true)
    public Page<JuzgadoRecordItem> getAll(String key, Pageable pageable) {
        String finalKey = (key != null) ? StringUtils.stripAccents(key).toLowerCase() : "";

        Page<Juzgado> page = juzgadoRepository
                .findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
        List<JuzgadoRecordItem> list = page.getContent().stream()
                .filter(juzgado -> finalKey.isEmpty() || StringUtils.stripAccents(juzgado.getNombre())
                        .toLowerCase()
                        .contains(finalKey))
                .map(juzgado -> new JuzgadoRecordItem(
                        juzgado.getId(),
                        juzgado.getNombre(),
                        juzgado.getEstado(),
                        (juzgado.getMateria() != null) ?
                                StringUtils
                                        .capitalize(StringUtils.stripAccents(juzgado.getMateria().getNombre()).toLowerCase()) : "- Archivo Judicial"))
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
        List<TipoJuicioRecord> tipoJuicios = Optional.ofNullable(juzgado.getTipoJuicios()).orElse(List.of()).stream()
                .map(tj -> new TipoJuicioRecord(
                        tj.getId(),
                        tj.getNombre(), null, null))
                .toList();
        List<JuzgadoContadorConfig> contadoresJuzgados = contadorJuzgadoRepository
                .findByJuzgadoIdAndEstado(juzgado.getId(), Estado.ACTIVE)
                .stream()
                .map(c -> new JuzgadoContadorConfig(
                        c.getTipoJuicio().getId(),
                        c.getTipoJuicio().getNombre(),
                        c.getMaxAsignaciones(),
                        c.getContadorAsignaciones()))
                .toList();
        if (juzgado.getInstanciaJuzgado() == null) {
            throw new IllegalStateException("InstanciaJuzgado no debe ser null");
        }
        return new JuzgadoRecord(
                juzgado.getId(),
                juzgado.getVersion(),
                juzgado.getNombre(),
                juzgado.getEstado(),
                (juzgado.getMateria() != null) ? juzgado.getMateria().getId() : null,
                juzgado.getSede().getId(),
                juzgado.getMaxAsignacionesRonda(),
                juzgado.getContadorAsignaciones(),
                juzgado.getInstanciaJuzgado().ordinal(),
                tipoJuicios,
                contadoresJuzgados);
    }

    public Juzgado findJuzgadoById(Integer id) {
        return juzgadoRepository.findByIdAndEstadoIn(id, List.of(Estado.ACTIVE, Estado.INACTIVE)).orElse(null);
    }

    public Juzgado requiredJuzgadoById(Integer id) {
        return this.juzgadoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Juzgado no encontrado", "id: " + id));
    }

    public JuzgadoRecordItem create(Juzgado juzgado) {
        List<JuzgadoContadorConfig> contadoresPayload = juzgado.getContadoresJuzgados();
        if (juzgadoRepository.findByNombreIgnoreCase(juzgado.getNombre()).isPresent()) {
            throw new ConflictException("No pueden existir 2 juzgados con el mismo nombre");
        }
        if (juzgado.getInstanciaJuzgado() != InstanciaJuzgado.NO_APLICA) { //NO ES ARCHIVO JUDICIAL
            Materia materia = materiaRepository.findById(juzgado.getMateria().getId())
                    .orElseThrow(() -> new NotFoundException("Materia no encontrada", "materiaId"));
            juzgado.setMateria(materia);

            juzgado.setContadorAsignaciones(0);
            if (juzgado.getMaxAsignacionesRonda() == null) {
                juzgado.setMaxAsignacionesRonda(2);
            }

            juzgado.setInstanciaJuzgado(TipoCarpeta.EXHORTO.name().equalsIgnoreCase(materia.getNombre())
                    ? InstanciaJuzgado.EXHORTO
                    : juzgado.getInstanciaJuzgado());
        } else {
            juzgado.setMateria(null);
        }

        Sede sede = sedeRepository.findById(juzgado.getSede().getId())
                .orElseThrow(() -> new NotFoundException("Sede no encontrada", "sedeId"));
        juzgado.setSede(sede);

        if (juzgado.getInstanciaJuzgado() != InstanciaJuzgado.NO_APLICA) { //NO ES ARCHIVO JUDICIAL
            List<Integer> tjIds = juzgado.getTipoJuicios().stream().map(TipoJuicio::getId).toList();
            List<TipoJuicio> tipojuicios = tipoJuicioRepository.findAllById(tjIds);
            juzgado.setTipoJuicios(tipojuicios);

            // Llena JuzgadoFolios para insertar en tabla JUZGADO_FOLIOS: crea folio para
            // exhorto_entrada
            // Añadir otra linea juzgadoFolios.add(); para añadir otro tipo de folio
            List<JuzgadoFolios> juzgadoFolios = new ArrayList<>();
            juzgadoFolios.add(new JuzgadoFolios().setTipoCarpeta(TipoCarpeta.DEMANDA).setJuzgado(juzgado));
            juzgadoFolios.add(new JuzgadoFolios().setTipoCarpeta(TipoCarpeta.EXHORTO).setJuzgado(juzgado));
            juzgadoFolios.add(new JuzgadoFolios().setTipoCarpeta(TipoCarpeta.APELACION).setJuzgado(juzgado));
            juzgadoFolios.add(new JuzgadoFolios().setTipoCarpeta(TipoCarpeta.DESPACHO).setJuzgado(juzgado));
            juzgadoFolios.add(new JuzgadoFolios().setTipoCarpeta(TipoCarpeta.APELACION_MUNICIPAL).setJuzgado(juzgado));
            juzgadoFolios.add(new JuzgadoFolios().setTipoCarpeta(TipoCarpeta.AMPARO).setJuzgado(juzgado));
            juzgado.setJuzgadoFolios(juzgadoFolios);
        }

        juzgado = juzgadoRepository.save(juzgado);
        juzgado.setContadoresJuzgados(contadoresPayload);
        syncContadoresJuzgados(juzgado);
        return new JuzgadoRecordItem(
                juzgado.getId(),
                juzgado.getNombre(),
                juzgado.getEstado(),
                (juzgado.getMateria() != null) ? juzgado.getMateria().getNombre() : "");
    }

    public JuzgadoRecordItem update(Juzgado juzgado) {
        List<JuzgadoContadorConfig> contadoresPayload = juzgado.getContadoresJuzgados();
        Optional<Juzgado> test = juzgadoRepository.findByNombreIgnoreCase(juzgado.getNombre());
        if (test.isPresent() && !Objects.equals(test.get().getId(), juzgado.getId())) {
            throw new ConflictException("No pueden existir 2 juzgados con el mismo nombre");
        }
        try {
            if (juzgado.getInstanciaJuzgado() == InstanciaJuzgado.NO_APLICA) {
                juzgado.setMateria(null);
                juzgado.setTipoJuicios(null);
            } else {
                juzgado.setMateria(materiaRepository.findById(juzgado.getMateria().getId())
                        .orElseThrow(() -> new NotFoundException("Materia no encontrada", "materiaId")));
                Juzgado juzgadoAsignaciones = juzgadoRepository.findById(juzgado.getId())
                        .orElseThrow(() -> new NotFoundException(JUZGADO_NOT_FOUND, "juzgadoId"));
                juzgado.setContadorAsignaciones(juzgadoAsignaciones.getContadorAsignaciones());
                List<Integer> tjIds = juzgado.getTipoJuicios().stream().map(TipoJuicio::getId).toList();
                List<TipoJuicio> tipojuicios = tipoJuicioRepository.findAllById(tjIds);
                juzgado.setTipoJuicios(tipojuicios);
            }
            juzgado.setSede(sedeRepository.findById(juzgado.getSede().getId())
                    .orElseThrow(() -> new NotFoundException("Sede no encontrada", "sedeId")));
            juzgado.setInstanciaJuzgado(juzgado.getInstanciaJuzgado());
            juzgado = juzgadoRepository.save(juzgado);
            juzgado.setContadoresJuzgados(contadoresPayload);
            syncContadoresJuzgados(juzgado);
            return new JuzgadoRecordItem(
                    juzgado.getId(),
                    juzgado.getNombre(),
                    juzgado.getEstado(),
                    (juzgado.getMateria() != null) ? juzgado.getMateria().getNombre() : "");
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

/**
 * Obtiene la ConexidadJuzgado asociada a la persona DocumentoItemRecord
 * que se encuentra en la base de datos.
 *
 * @param actor PersonaDocumentoItemRecord del actor
 * @param demandado PersonaDocumentItemRecord del demandado
 * @param tipoJuicio tipo de juicio al que se relaciona con el actor
 * @return la ConexidadJuzgado asociada al actor, o null si no se encuentra
 */
    public Juzgado getConexidadJuzgado(PersonaDocumentoItemRecord actor, PersonaDocumentoItemRecord demandado,
                                       TipoJuicio tipoJuicio) {
        List<Carpeta> carpetas = new ArrayList<>();

        TipoPartes actorParte = tipoPartesRepository.findByNombreAndTipoJuicioId("Actor", tipoJuicio.getId())
                .orElseThrow(() -> new NotFoundException("Tipo Parte no encontrado", actor.tipoParte().toString()));
        
        TipoPartes demandadoParte = tipoPartesRepository.findByNombreAndTipoJuicioId("Demandado", tipoJuicio.getId())
                .orElseThrow(() -> new NotFoundException("Tipo Parte no encontrado", demandado.tipoParte().toString()));

        List<PersonaDocumento> registrosActor = personaDocumentoRepository
                .findByNombreIgnoreCaseAndApellidoPaternoIgnoreCaseAndApellidoMaternoIgnoreCaseAndPseudonimoIgnoreCaseAndTipoPartesId(
                        actor.nombre(), actor.apellidoPaterno(), actor.apellidoMaterno(), actor.pseudonimo(),
                        actorParte.getId());

        List<PersonaDocumento> registrosDemandado = personaDocumentoRepository
                .findByNombreIgnoreCaseAndApellidoPaternoIgnoreCaseAndApellidoMaternoIgnoreCaseAndPseudonimoIgnoreCaseAndTipoPartesId(
                        demandado.nombre(), demandado.apellidoPaterno(), demandado.apellidoMaterno(),
                        demandado.pseudonimo(), demandadoParte.getId());
        
        //SI el tipo de juicio es familiar oralidad se busca por CURP.
        /*
        if(tipoJuicio.getTipoSistema().getNombre().equals("Oral")){
                List<PersonaDocumento> registrosActorCurp = personaDocumentoRepository
                        .findByCurpAndTipoPartesId(actor.curp(), actorParte.getId());

                List<PersonaDocumento> registrosDemandadoCurp = personaDocumentoRepository
                        .findByCurpAndTipoPartesId(demandado.curp(), demandadoParte.getId());
                
                registrosActor.addAll(registrosActorCurp);
                registrosDemandado.addAll(registrosDemandadoCurp);

        }
        */

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

            if (juzgadoRepository.findByMateriaAndEstado(tipoJuicio.getMateria(), Estado.ACTIVE)
                    .contains(carpeta.getJuzgado()))
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
        } else if (TipoCarpeta.EXHORTO.name().toUpperCase().equals(tipoCarpeta.name())) {
            instanciaJuzgado = InstanciaJuzgado.EXHORTO;
            reason = "No se encontró un Juzgado de la materia " + tipoJuicio.getMateria().getNombre()
                    + " para asignar. ";
        } else {
            instanciaJuzgado = InstanciaJuzgado.PRIMERA_INSTANCIA;
            reason = "No hay juzgados relacionados a la Oficialia";
        }

        if (juzgadosRelacionados.isEmpty()) {
            throw new NotFoundException(reason, "juzgadosRelacionados");
        }

        if (isCasoEspecialContadorJuzgado(tipoJuicio, tipoCarpeta)) {
            return getJuzgadoConContadorEspecial(tipoJuicio, juzgadosRelacionados);
        }

        List<Juzgado> juzgados = juzgadoRepository.findJuzgadosMenosAsignaciones(tipoJuicio.getMateria(),
                instanciaJuzgado,
                juzgadosRelacionados.stream().map(Juzgado::getId).toList());

        if (juzgados.isEmpty()) {

            revisarCargaJuzgados(tipoJuicio.getMateria(), tipoCarpeta, juzgadosRelacionados);
            juzgados = juzgadoRepository.findJuzgadosMenosAsignaciones(tipoJuicio.getMateria(), instanciaJuzgado,
                    juzgadosRelacionados.stream().map(Juzgado::getId).toList());

            if (juzgados.isEmpty()) {
                if (TipoCarpeta.APELACION.name().equals(tipoCarpeta.name())) {
                    throw new NotFoundException(reason, tipoJuicio.getNombre());
                }
                throw (new NotFoundException(reason, tipoJuicio.getNombre()));
            }

        }

        int rand = RANDOM.nextInt(juzgados.size());

        return juzgados.get(rand);

    }

    private Juzgado getJuzgadoConContadorEspecial(TipoJuicio tipoJuicio, List<Juzgado> juzgadosRelacionados) {
        List<Integer> juzgadosIds = juzgadosRelacionados.stream().map(Juzgado::getId).toList();
        List<Juzgado> juzgados = contadorJuzgadoRepository.findJuzgadosMenosAsignaciones(juzgadosIds, tipoJuicio.getId());

        if (juzgados.isEmpty()) {
            revisarCargaContadoresJuzgados(tipoJuicio.getId(), juzgadosIds);
            juzgados = contadorJuzgadoRepository.findJuzgadosMenosAsignaciones(juzgadosIds, tipoJuicio.getId());
        }

        if (juzgados.isEmpty()) {
            throw new NotFoundException(
                    "No hay contadores de juzgado disponibles para el tipo de juicio seleccionado",
                    tipoJuicio.getNombre());
        }

        return juzgados.get(RANDOM.nextInt(juzgados.size()));
    }

    private boolean isCasoEspecialContadorJuzgado(TipoJuicio tipoJuicio, TipoCarpeta tipoCarpeta) {
        if (!TipoCarpeta.DEMANDA.equals(tipoCarpeta) || tipoJuicio == null || tipoJuicio.getMateria() == null) {
            return false;
        }
        String materia = Optional.ofNullable(tipoJuicio.getMateria().getNombre()).orElse("").toLowerCase();
        String nombreTipoJuicio = Optional.ofNullable(tipoJuicio.getNombre()).orElse("").toLowerCase();
        return materia.contains("mercantil") && nombreTipoJuicio.contains("oral");
    }

    private void syncContadoresJuzgados(Juzgado juzgado) {
        List<ContadorJuzgado> contadoresActivos = contadorJuzgadoRepository.findByJuzgadoIdAndEstado(juzgado.getId(), Estado.ACTIVE);
        Map<Integer, ContadorJuzgado> contadorByTipoJuicioId = contadoresActivos.stream()
                .collect(Collectors.toMap(c -> c.getTipoJuicio().getId(), c -> c, (left, right) -> left));

        List<TipoJuicio> tipoJuicios = Optional.ofNullable(juzgado.getTipoJuicios()).orElse(List.of());
        Set<Integer> tipoJuiciosEspeciales = tipoJuicios.stream()
                .filter(tj -> isJuicioMercantilOral(juzgado.getMateria(), tj))
                .map(TipoJuicio::getId)
                .collect(Collectors.toSet());

        Map<Integer, JuzgadoContadorConfig> configuracionByTipoJuicioId = Optional.ofNullable(juzgado.getContadoresJuzgados())
                .orElse(List.of())
                .stream()
                .filter(cfg -> cfg.getTipoJuicioId() != null)
                .collect(Collectors.toMap(JuzgadoContadorConfig::getTipoJuicioId, cfg -> cfg, (left, right) -> right));

        if (!tipoJuiciosEspeciales.isEmpty()) {
            List<String> faltantes = tipoJuicios.stream()
                    .filter(tj -> tipoJuiciosEspeciales.contains(tj.getId()))
                    .filter(tj -> {
                        JuzgadoContadorConfig config = configuracionByTipoJuicioId.get(tj.getId());
                        return config == null || config.getMaxAsignaciones() == null || config.getMaxAsignaciones() < 0;
                    })
                    .map(tj -> tj.getId() + " - " + tj.getNombre())
                    .toList();

            if (!faltantes.isEmpty()) {
                throw new ConflictException(
                        "Debe capturar el maximo de asignaciones para cada tipo de juicio oral mercantil. Faltantes: "
                                + String.join(", ", faltantes));
            }
        }

        for (Integer tipoJuicioId : tipoJuiciosEspeciales) {
            JuzgadoContadorConfig config = configuracionByTipoJuicioId.get(tipoJuicioId);
            ContadorJuzgado contador = contadorByTipoJuicioId.get(tipoJuicioId);

            if (contador == null) {
                contador = new ContadorJuzgado();
                contador.setJuzgado(juzgado);
                contador.setTipoJuicio(tipoJuicioRepository.findById(tipoJuicioId)
                        .orElseThrow(() -> new NotFoundException("Tipo de juicio no encontrado", "tipoJuicioId")));
                contador.setContadorAsignaciones(0);
            }

            contador.setMaxAsignaciones(config.getMaxAsignaciones());
            if (config.getContadorAsignaciones() != null && config.getContadorAsignaciones() >= 0) {
                contador.setContadorAsignaciones(config.getContadorAsignaciones());
            }
            contador.setEstado(Estado.ACTIVE);
            contadorJuzgadoRepository.save(contador);
        }

        for (ContadorJuzgado contador : contadoresActivos) {
            if (!tipoJuiciosEspeciales.contains(contador.getTipoJuicio().getId())) {
                contador.setEstado(Estado.INACTIVE);
                contadorJuzgadoRepository.save(contador);
            }
        }
    }

    private boolean isJuicioMercantilOral(Materia materia, TipoJuicio tipoJuicio) {
        if (materia == null || tipoJuicio == null) {
            return false;
        }
        String materiaNombre = Optional.ofNullable(materia.getNombre()).orElse("").toLowerCase();
        String tipoJuicioNombre = Optional.ofNullable(tipoJuicio.getNombre()).orElse("").toLowerCase();
        return materiaNombre.contains("mercantil") && tipoJuicioNombre.contains("oral");
    }

    public void actualizarCarga(Juzgado juzgado, TipoCarpeta tipoCarpeta, List<Juzgado> juzgadosRelacionados) {
        juzgadoRepository.actualizarContadorAsignaciones(juzgado.getId());
        revisarCargaJuzgados(juzgado.getMateria(), tipoCarpeta, juzgadosRelacionados);
    }

    public void actualizarCarga(Juzgado juzgado, TipoCarpeta tipoCarpeta, List<Juzgado> juzgadosRelacionados,
                                TipoJuicio tipoJuicio) {
        if (isCasoEspecialContadorJuzgado(tipoJuicio, tipoCarpeta)) {
            int rows = contadorJuzgadoRepository.actualizarContadorAsignaciones(juzgado.getId(), tipoJuicio.getId());
            if (rows == 0) {
                throw new NotFoundException(
                        "No existe contador configurado para el juzgado y tipo de juicio seleccionado",
                        "contadorJuzgado");
            }
            revisarCargaContadoresJuzgados(
                    tipoJuicio.getId(),
                    juzgadosRelacionados.stream().map(Juzgado::getId).toList());
            return;
        }
        actualizarCarga(juzgado, tipoCarpeta, juzgadosRelacionados);
    }

    public void revisarCargaJuzgados(Materia materia, TipoCarpeta tipoCarpeta, List<Juzgado> juzgadosRelacionados) {

        InstanciaJuzgado instanciaJuzgado;
        if (TipoCarpeta.APELACION.equals(tipoCarpeta)) {
            instanciaJuzgado = InstanciaJuzgado.SEGUNDA_INSTANCIA;

        } else if (TipoCarpeta.EXHORTO.equals(tipoCarpeta)) {
            instanciaJuzgado = InstanciaJuzgado.EXHORTO;
        } else {
            instanciaJuzgado = InstanciaJuzgado.PRIMERA_INSTANCIA;
        }

        List<Integer> idsJuzgadosRelacionados = juzgadosRelacionados.stream().map(juzgado -> juzgado.getId()).toList();

        int totalAsignaciones = juzgadoRepository.sumContadorAsignacionesByMateria(materia, instanciaJuzgado);
        int totalMaxAsignaciones = juzgadoRepository.sumMaxAsignacionesRondaByMateria(materia, instanciaJuzgado);
        int totalJuzgadosMenosAsignaciones = juzgadoRepository
                .findJuzgadosMenosAsignaciones(materia, instanciaJuzgado, idsJuzgadosRelacionados).size();

        if (totalAsignaciones >= totalMaxAsignaciones && totalJuzgadosMenosAsignaciones == 0) {
            juzgadoRepository.reiniciarContadorAsignaciones(materia, instanciaJuzgado);
        }
    }

    private void revisarCargaContadoresJuzgados(Integer tipoJuicioId, List<Integer> juzgadosIds) {
        if (juzgadosIds == null || juzgadosIds.isEmpty()) {
            return;
        }
        int totalAsignaciones = contadorJuzgadoRepository.sumContadorAsignaciones(juzgadosIds, tipoJuicioId);
        int totalMaxAsignaciones = contadorJuzgadoRepository.sumMaxAsignaciones(juzgadosIds, tipoJuicioId);
        int totalJuzgadosMenosAsignaciones = contadorJuzgadoRepository
                .findJuzgadosMenosAsignaciones(juzgadosIds, tipoJuicioId).size();

        if (totalMaxAsignaciones > 0 && totalAsignaciones >= totalMaxAsignaciones && totalJuzgadosMenosAsignaciones == 0) {
            contadorJuzgadoRepository.reiniciarContadorAsignaciones(juzgadosIds, tipoJuicioId);
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
        Integer idCentroTrabajo = personaLogueada.getJuzgado() != null ? personaLogueada.getJuzgado().getId()
                : oficialiaId;

        return juzgadoRepository.findAllByEstadoAutocomplete(Estado.ACTIVE, key, centroTrabajo, idCentroTrabajo);
    }

    @Transactional(readOnly = true)
    public List<JuzgadoRecordItem> findbyEstadoActiveAndInactive() {
        Persona personaLogueada = personaService.getAuditor();

        String oficialia = personaLogueada.getOficialia() != null ? "Oficialia" : null;
        Integer oficialiaId = personaLogueada.getOficialia() != null ? personaLogueada.getOficialia().getId() : null;

        String centroTrabajo = personaLogueada.getJuzgado() != null ? "Juzgado" : oficialia;
        Integer idCentroTrabajo = personaLogueada.getJuzgado() != null ? personaLogueada.getJuzgado().getId()
                : oficialiaId;

        return juzgadoRepository.findbyEstadoActiveAndInactive(centroTrabajo, idCentroTrabajo);
    }

    public JuzgadoRecordItem updateStatus(Integer id, Integer status) {
        Estado estado = Estado.values()[status];
        Juzgado juzgado = juzgadoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(JUZGADO_NOT_FOUND, id.toString()));
        juzgado.setEstado(estado);
        juzgadoRepository.save(juzgado);

        return new JuzgadoRecordItem(id, juzgado.getNombre(), juzgado.getEstado(), "");
    }

    public List<JuzgadoRecordItem> findAllByInstancia(InstanciaJuzgado instanciaJuzgado) {
        return juzgadoRepository.findAllByInstancia(instanciaJuzgado);
    }

    public JuzgadoRecordItem getJuzgadoActual() {
        Persona persona = personaService.getAuditor();

        if (persona.getJuzgado() == null) {
            throw new NotFoundException("El usuario actual no esta asignado a un juzgado", "Juzgado");
        }

        Juzgado juzgadoActual = persona.getJuzgado();

        return new JuzgadoRecordItem(juzgadoActual.getId(), juzgadoActual.getNombre(), juzgadoActual.getEstado(),
                juzgadoActual.getMateria().getNombre());
    }

    public Optional<Juzgado> findByClaveJuzgado(String clave) {
        return juzgadoRepository.findByClaveJuzgado(clave);
    }
}
