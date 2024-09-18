package mx.gob.pjpuebla.trials.core.juzgados;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRecord;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.reljuzgadotipojuicio.RelJuzgadoTipoJuicio;
import mx.gob.pjpuebla.trials.core.reljuzgadotipojuicio.RelJuzgadoTipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRecord;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRecord;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoDTO;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    private final TipoJuicioRepository tipoJuicioRepository;
    private final RelJuzgadoTipoJuicioRepository relJuzgadoTipoJuicioRepository;

    private static final String JUZGADO_ID = "juzgadoId";

    @Transactional(readOnly = true)
    public Page<JuzgadoRecordResponse> getAll(Juzgado example, Pageable pageable) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<Juzgado> page = juzgadoRepository.findAll(Example.of(example, exampleMatcher), pageable);

        List<JuzgadoRecordResponse> list = page.getContent().stream().map(juzgado -> new JuzgadoRecordResponse(juzgado.getId(), juzgado.getNombre(), juzgado.getEstado(), juzgado.getMateria().getNombre())).toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public JuzgadoDTO findById(Integer id) {
        JuzgadoDTO juzgadoDTO = new JuzgadoDTO();
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        JuzgadoRecord juzgadoRecord = juzgadoRepository.findByIdAndEstadoIn(id, estados).orElseThrow(() -> new NotFoundException("Juzgado no encontrado", JUZGADO_ID));
        juzgadoDTO.setJuzgado(
                new Juzgado()
                        .setId(juzgadoRecord.id())
                        .setNombre(juzgadoRecord.nombre())
                        .setVersion(juzgadoRecord.version())
                        .setEstado(juzgadoRecord.estado())
                        .setMateria(new Materia().setId(juzgadoRecord.materiaId()))
                        .setSede(new Sede().setId(juzgadoRecord.sedeId()))
        );

        List<RelJuzgadoTipoJuicio> relJuzgadoTipoJuicioList = relJuzgadoTipoJuicioRepository.findAllByjuzgado(juzgadoDTO.getJuzgado());

        List<TipoJuicioRecord> tipoJuicioRecords = relJuzgadoTipoJuicioList.stream()
                .map(rel -> new TipoJuicioRecord(
                        rel.getTipoJuicio().getId(),
                        rel.getTipoJuicio().getNombre(),
                        new TipoSistemaRecord(
                                rel.getTipoJuicio().getTipoSistema().getId(),
                                rel.getTipoJuicio().getTipoSistema().getNombre()
                        ),
                        new MateriaRecord(
                                rel.getTipoJuicio().getMateria().getId(),
                                rel.getTipoJuicio().getMateria().getNombre()
                        )
                ))
                .collect(Collectors.toList());
        juzgadoDTO.setTipoJuicio(tipoJuicioRecords);
        return juzgadoDTO;
    }

    public List<JuzgadoRecordResponse> getAllWithoutPagination() {
        return juzgadoRepository.findAllByEstadoIn(Arrays.asList(Estado.ACTIVE, Estado.INACTIVE));
    }

    public JuzgadoRecordResponse create(Juzgado juzgado, List<TipoJuicioRecord> tipoJuicioRecord) {
        //Llena y guarda juzgado
        juzgado.setMateria(materiaRepository.findById(juzgado.getMateria().getId()).orElseThrow(() -> new NotFoundException("Materia no encontrada", "materiaId")));
        juzgado.setSede(sedeRepository.findById(juzgado.getSede().getId()).orElseThrow(() -> new NotFoundException("Sede no encontrada", "sedeId")));
        juzgado = juzgadoRepository.save(juzgado);
        juzgadoRepository.generarSecuenciaExpediente(juzgado.getId());

        //llena y guarda la tabla de relación: RelJuzgadoTipoJuicio
        List<Integer> idsTipoJuicio = getIdTipoJuicioList(tipoJuicioRecord);
        for (Integer idTipoJuicio : idsTipoJuicio) {
            RelJuzgadoTipoJuicio relJuzgadoTipoJuicio = new RelJuzgadoTipoJuicio();
            relJuzgadoTipoJuicio.setJuzgado(juzgado);
            relJuzgadoTipoJuicio.setTipoJuicio(tipoJuicioRepository.findById(idTipoJuicio).orElseThrow(() -> new NotFoundException("Tipo Juicio no encontrado", "tipoJuicioId")));
            relJuzgadoTipoJuicioRepository.save(relJuzgadoTipoJuicio);
        }
        return new JuzgadoRecordResponse(juzgado.getId(), juzgado.getNombre(), juzgado.getEstado(), juzgado.getMateria().getNombre());
    }

    public JuzgadoRecordResponse update(Juzgado juzgado, List<TipoJuicioRecord> tipoJuicioRecord) {
        try {
            Juzgado juzgadoUpdate = juzgadoRepository.findById(juzgado.getId()).orElseThrow(() -> new NotFoundException("Juzgado no encontrado", JUZGADO_ID));
            juzgadoUpdate.setNombre(juzgado.getNombre());
            juzgadoUpdate.setEstado(juzgado.getEstado());
            juzgadoUpdate.setMateria(materiaRepository.findById(juzgado.getMateria().getId()).orElseThrow(() -> new NotFoundException("Materia no encontrada", "materiaId")));
            juzgadoUpdate.setSede(sedeRepository.findById(juzgado.getSede().getId()).orElseThrow(() -> new NotFoundException("Sede no encontrada", "sedeId")));
            juzgadoUpdate = juzgadoRepository.save(juzgadoUpdate);

            //Borra las relaciones de Juzgado-TipoJuicio para insertar relaciones nuevas
            deleteRelJuzgadoTipoJuicioByJuzgado(juzgado);
            List<Integer> idsTipoJuicio = getIdTipoJuicioList(tipoJuicioRecord);
            for (Integer idTipoJuicio : idsTipoJuicio) {
                RelJuzgadoTipoJuicio relJuzgadoTipoJuicio = new RelJuzgadoTipoJuicio();
                relJuzgadoTipoJuicio.setJuzgado(juzgado);
                relJuzgadoTipoJuicio.setTipoJuicio(tipoJuicioRepository.findById(idTipoJuicio).orElseThrow(() -> new NotFoundException("Tipo Juicio no encontrado", "tipoJuicioId")));
                relJuzgadoTipoJuicioRepository.save(relJuzgadoTipoJuicio);
            }

            return new JuzgadoRecordResponse(juzgadoUpdate.getId(), juzgadoUpdate.getNombre(), juzgadoUpdate.getEstado(), juzgadoUpdate.getMateria().getNombre());
        } catch (OptimisticLockingFailureException ex) {
            log.error("update -> {}", ex);
            throw new mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException("Juzgado modificado por otro usuario", JUZGADO_ID);
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
        //TODO. Descartar mayusculas minusculas
        List<PersonaDocumento> registrosActor = personaDocumentoRepository.findByNombreAndApellidoPaternoAndApellidoMaternoAndPseudonimoAndTipoPartesId(actor.getNombre(), actor.getApellidoPaterno(), actor.getApellidoMaterno(), actor.getPseudonimo(), actorParte.getId());

        List<PersonaDocumento> registrosDemandado = personaDocumentoRepository.findByNombreAndApellidoPaternoAndApellidoMaternoAndPseudonimoAndTipoPartesId(demandado.getNombre(), demandado.getApellidoPaterno(), demandado.getApellidoMaterno(), demandado.getPseudonimo(), demandadoParte.getId());

        if (registrosActor.isEmpty() || registrosDemandado.isEmpty()) {
            return null;
        }

        for (PersonaDocumento tmp : registrosActor) {
            documentos.add(tmp.getDocumento());
        }

        for (PersonaDocumento tmp : registrosDemandado) {
            Documento documento;

            if (!documentos.contains(tmp.getDocumento())) continue;

            documento = tmp.getDocumento();

            if (juzgadoRepository.findByMateriaAndEstado(tipoJuicio.getMateria(), Estado.ACTIVE).contains(documento.getJuzgado()))
                return documento.getJuzgado();
        }
        return null;
    }

    private List<Integer> getIdTipoJuicioList(List<TipoJuicioRecord> list) {
        List<Integer> idsTipoJuicio = new ArrayList<>();
        list.stream().forEach(tipoJuicioRecord -> idsTipoJuicio.add(tipoJuicioRecord.id()));
        return idsTipoJuicio;
    }

    private void deleteRelJuzgadoTipoJuicioByJuzgado(Juzgado juzgado) {
        List<RelJuzgadoTipoJuicio> relJuzgadoTipoJuicioList = relJuzgadoTipoJuicioRepository.findAllByjuzgado(juzgado);
        relJuzgadoTipoJuicioRepository.deleteAll(relJuzgadoTipoJuicioList);
    }
}