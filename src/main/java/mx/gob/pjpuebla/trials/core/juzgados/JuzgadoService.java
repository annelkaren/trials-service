package mx.gob.pjpuebla.trials.core.juzgados;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRecord;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoItemRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class JuzgadoService {

    private final JuzgadoRepository juzgadoRepository;
    private final TipoJuicioRepository tipoJuicioRepository;
    private final SedeRepository sedeRepository;
    private final MateriaRepository materiaRepository;
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final TipoPartesRepository tipoPartesRepository;

    private static final Random RANDOM = new Random();

    @Transactional(readOnly = true)
    public Page<JuzgadoRecordItem> getAll(Juzgado example, Pageable pageable) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<Juzgado> page = juzgadoRepository.findAll(Example.of(example, exampleMatcher), pageable);

        List<JuzgadoRecordItem> list = page.getContent().stream()
                .map(juzgado -> new JuzgadoRecordItem(
                        juzgado.getId(),
                        juzgado.getNombre(),
                        juzgado.getEstado(),
                        juzgado.getMateria().getNombre()
                )).toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public JuzgadoRecord findById(Integer id) {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Juzgado juzgado = juzgadoRepository.findByIdAndEstadoIn(id, estados)
                .orElseThrow(() -> new NotFoundException("Juzgado no encontrado", "juzgadoId"));
        List<TipoJuicioRecord> tipoJuicios = juzgado.getTipoJuicios().stream()
                .map(tj -> new TipoJuicioRecord(
                        tj.getId(),
                        tj.getNombre(), null, null
                )).toList();
        return new JuzgadoRecord(
                juzgado.getId(),
                juzgado.getVersion(),
                juzgado.getNombre(),
                juzgado.getEstado(),
                juzgado.getMateria().getId(),
                juzgado.getSede().getId(),
                juzgado.getMaxAsignacionesRonda(),
                juzgado.getContadorAsignaciones(),
                tipoJuicios
        );
    }

    public JuzgadoRecordItem create(Juzgado juzgado) {
        Materia materia = materiaRepository.findById(juzgado.getMateria().getId()).orElseThrow(() -> new NotFoundException("Materia no encontrada", "materiaId"));
        juzgado.setMateria(materia);

        Sede sede = sedeRepository.findById(juzgado.getSede().getId()).orElseThrow(() -> new NotFoundException("Sede no encontrada", "sedeId"));
        juzgado.setSede(sede);

        juzgado.setContadorAsignaciones(0);

        List<Integer> tjIds = juzgado.getTipoJuicios().stream().map(TipoJuicio::getId).toList();
        List<TipoJuicio> tipojuicios = tipoJuicioRepository.findAllById(tjIds);
        juzgado.setTipoJuicios(tipojuicios);

        juzgado = juzgadoRepository.save(juzgado);
        juzgadoRepository.generarSecuenciaExpediente(juzgado.getId());
        return new JuzgadoRecordItem(
                juzgado.getId(),
                juzgado.getNombre(),
                juzgado.getEstado(),
                juzgado.getMateria().getNombre()
        );
    }

    public JuzgadoRecordItem update(Juzgado juzgado) {
        try {
            juzgado.setMateria(materiaRepository.findById(juzgado.getMateria().getId()).orElseThrow(() -> new NotFoundException("Materia no encontrada", "materiaId")));
            juzgado.setSede(sedeRepository.findById(juzgado.getSede().getId()).orElseThrow(() -> new NotFoundException("Sede no encontrada", "sedeId")));

            List<Integer> tjIds = juzgado.getTipoJuicios().stream().map(TipoJuicio::getId).toList();
            List<TipoJuicio> tipojuicios = tipoJuicioRepository.findAllById(tjIds);
            juzgado.setTipoJuicios(tipojuicios);

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
        juzgadoRepository.deleteById(id);
        juzgadoRepository.eliminarSecuenciaExpediente(id);
    }

    public NumeroExpedienteRecord getNumeroExpediente(Integer id) {
        return new NumeroExpedienteRecord(juzgadoRepository.getNumeroExpediente(id));
    }

    public Boolean reiniciarSecuenciasExpedientes() {
        return juzgadoRepository.reiniciarSecuenciasExpedientes();
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

    public Juzgado getJuzgado(TipoJuicio tipoJuicio) {
        List<Juzgado> juzgados = juzgadoRepository.findJuzgadosMenosAsignaciones(tipoJuicio.getMateria());

        if (juzgados.isEmpty()) {
            revisarCargaJuzgados(tipoJuicio.getMateria());

            juzgados = juzgadoRepository.findJuzgadosMenosAsignaciones(tipoJuicio.getMateria());

            if (juzgados.isEmpty())
                throw (new NotFoundException("No se puede asignar un Juzgado", tipoJuicio.getNombre()));
        }

        int rand = RANDOM.nextInt(juzgados.size());

        return juzgados.get(rand);

    }

    public void actualizarCarga(Juzgado juzgado) {
        juzgadoRepository.actualizarContadorAsignaciones(juzgado.getId());
        revisarCargaJuzgados(juzgado.getMateria());
    }

    public void revisarCargaJuzgados(Materia materia) {
        int totalAsignaciones = juzgadoRepository.sumContadorAsignacionesByMateria(materia);
        int totalMaxAsignaciones = juzgadoRepository.sumMaxAsignacionesRondaByMateria(materia);
        int totalJuzgadosMenosAsignaciones = juzgadoRepository.findJuzgadosMenosAsignaciones(materia).size();

        if (totalAsignaciones >= totalMaxAsignaciones && totalJuzgadosMenosAsignaciones == 0) {
            juzgadoRepository.reiniciarContadorAsignaciones(materia);
        }
    }
}