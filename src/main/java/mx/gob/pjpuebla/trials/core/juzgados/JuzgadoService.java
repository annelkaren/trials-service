package mx.gob.pjpuebla.trials.core.juzgados;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoDTO;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class JuzgadoService {

    private final JuzgadoRepository juzgadoRepository;
    private final SedeRepository sedeRepository;
    private final MateriaRepository materiaRepository;
    private final PersonaDocumentoRepository personaDocumentoRepository;
    private final TipoPartesRepository tipoPartesRepository;

    private static final Random RANDOM = new Random();

    @Transactional(readOnly = true)
    public Page<JuzgadoRecordResponse> getAll(Juzgado example, Pageable pageable) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<Juzgado> page = juzgadoRepository.findAll(Example.of(example, exampleMatcher), pageable);

        List<JuzgadoRecordResponse> list = page.getContent().stream()
                .map(juzgado -> new JuzgadoRecordResponse(
                        juzgado.getId(), juzgado.getNombre(), juzgado.getEstado(),
                        juzgado.getMateria().getNombre(), juzgado.getMaxAsignacionesRonda(), juzgado.getContadorAsignaciones()))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public JuzgadoRecord findById(Integer id) {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        return juzgadoRepository.findByIdAndEstadoIn(id, estados)
                .orElseThrow(() -> new NotFoundException("Juzgado no encontrado", "juzgadoId"));
    }

    public List<JuzgadoRecordResponse> getAllWithoutPagination() {
        return juzgadoRepository.findAllByEstadoIn(Arrays.asList(Estado.ACTIVE, Estado.INACTIVE));
    }

    public JuzgadoRecordResponse create(Juzgado juzgado) {
        juzgado.setMateria(materiaRepository.findById(juzgado.getMateria().getId()).orElse(null));
        juzgado.setSede(sedeRepository.findById(juzgado.getSede().getId()).orElse(null));
        juzgado.setContadorAsignaciones(0);
        juzgado = juzgadoRepository.save(juzgado);
        juzgadoRepository.generarSecuenciaExpediente(juzgado.getId());
        return new JuzgadoRecordResponse(juzgado.getId(), juzgado.getNombre(), juzgado.getEstado(), juzgado.getMateria().getNombre(),
                juzgado.getMaxAsignacionesRonda(), juzgado.getContadorAsignaciones());
    }

    public JuzgadoRecordResponse update(Juzgado juzgado) {
        try {
            juzgado.setMateria(materiaRepository.findById(juzgado.getMateria().getId()).orElseThrow(() -> new NotFoundException("Materia no encontrada", "materiaId")));
            juzgado.setSede(sedeRepository.findById(juzgado.getSede().getId()).orElseThrow(() -> new NotFoundException("Sede no encontrada", "sedeId")));
            if (juzgado.getContadorAsignaciones() == null) {//TODO eliminar fix para demo
                juzgado.setContadorAsignaciones(0);
            }
            juzgado = juzgadoRepository.save(juzgado);
            return new JuzgadoRecordResponse(juzgado.getId(), juzgado.getNombre(), juzgado.getEstado(), juzgado.getMateria().getNombre(),
                    juzgado.getMaxAsignacionesRonda(), juzgado.getContadorAsignaciones());
        } catch (org.springframework.dao.OptimisticLockingFailureException ex) {
            log.error("update -> {}", ex);
            throw new mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException("Juzgado modificado por otro usuario", "juzgadoId");
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

    public Juzgado getConexidadJuzgado(PersonaDocumentoDTO actor, PersonaDocumentoDTO demandado, TipoJuicio tipoJuicio) {
        List<Documento> documentos = new ArrayList<>();
        TipoPartes actorParte = tipoPartesRepository.findByNombreAndTipoJuicioId("Actor", tipoJuicio.getId()).orElseThrow(() -> new NotFoundException("Tipo Parte no encontrado", actor.getTipoParte().toString()));
        TipoPartes demandadoParte = tipoPartesRepository.findByNombreAndTipoJuicioId("Demandado", tipoJuicio.getId()).orElseThrow(() -> new NotFoundException("Tipo Parte no encontrado", demandado.getTipoParte().toString()));

        List<PersonaDocumento> registrosActor = personaDocumentoRepository
                .findByNombreIgnoreCaseAndApellidoPaternoIgnoreCaseAndApellidoMaternoIgnoreCaseAndPseudonimoIgnoreCaseAndTipoPartesId(
                        actor.getNombre(), actor.getApellidoPaterno(), actor.getApellidoMaterno(), actor.getPseudonimo(), actorParte.getId());

        List<PersonaDocumento> registrosDemandado = personaDocumentoRepository
                .findByNombreIgnoreCaseAndApellidoPaternoIgnoreCaseAndApellidoMaternoIgnoreCaseAndPseudonimoIgnoreCaseAndTipoPartesId(
                        demandado.getNombre(), demandado.getApellidoPaterno(), demandado.getApellidoMaterno(), demandado.getPseudonimo(), demandadoParte.getId());

        if (registrosActor.isEmpty() || registrosDemandado.isEmpty()) {
            return null;
        }

        for (PersonaDocumento tmp : registrosActor) {
            documentos.add(tmp.getDocumento());
        }

        for (PersonaDocumento tmp : registrosDemandado) {
            Documento documento;

            if (!documentos.contains(tmp.getDocumento()))
                continue;

            documento = tmp.getDocumento();

            if (juzgadoRepository.findByMateriaAndEstado(tipoJuicio.getMateria(), Estado.ACTIVE).contains(documento.getJuzgado()))
                return documento.getJuzgado();
        }
        return null;
    }

    public Juzgado getJuzgado(TipoJuicio tipoJuicio) {
        int rand = 0;
        List<Juzgado> juzgados = juzgadoRepository.findJuzgadosMenosAsignaciones(tipoJuicio.getMateria());

        if (juzgados.isEmpty()) {
            revisarCargaJuzgados(tipoJuicio.getMateria());

            juzgados = juzgadoRepository.findJuzgadosMenosAsignaciones(tipoJuicio.getMateria());

            if (juzgados.isEmpty())
                throw (new NotFoundException("No se puede asignar un Juzgado", tipoJuicio.getNombre()));
        }

        rand = RANDOM.nextInt(juzgados.size());

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