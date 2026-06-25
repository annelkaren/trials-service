package mx.gob.pjpuebla.trials.core.tipoacuerdo;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaRecord;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRecord;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Transactional
@RequiredArgsConstructor
@Service
public class TipoAcuerdoService {

    private final TipoAcuerdoRepository tipoAcuerdoRepository;
    private final CarpetaRepository carpetaRepository;
    private final JuzgadoRepository juzgadoRepository;
    private static final String MATERIA = "FAMILIAR";

    public List<TipoAcuerdoRecord> findByDocumentoId(Integer carpetaId) {

        Carpeta carpeta = carpetaRepository.findById(carpetaId)
                .orElseThrow(() -> new NotFoundException("Carpeta no encontrado " , carpetaId.toString()));

        MateriaRecord materiaRecord = null;
        TipoSistemaRecord tipoSistemaRecord = null;

        Integer documentoMateriaId = carpeta.getTipoJuicio().getMateria().getId();
        String documentoNombre = carpeta.getTipoJuicio().getMateria().getNombre();

        if (documentoMateriaId != null && documentoNombre != null) {
            materiaRecord = new MateriaRecord(documentoMateriaId, documentoNombre);
        }

        Integer documentoTipoSistemaId = carpeta.getTipoJuicio().getTipoSistema() != null ?
                carpeta.getTipoJuicio().getTipoSistema().getId() : null;

        String documentoTipoSistemaNombre = carpeta.getTipoJuicio().getTipoSistema() != null ?
                carpeta.getTipoJuicio().getTipoSistema().getNombre() : null;

        if (documentoTipoSistemaId != null && documentoTipoSistemaNombre != null) {
            tipoSistemaRecord = new TipoSistemaRecord(documentoTipoSistemaId, documentoTipoSistemaNombre);
        }
        List<TipoAcuerdo> tipoAcuerdos = null;

        if (materiaRecord != null && materiaRecord.nombre().equalsIgnoreCase(MATERIA)) {
            if (tipoSistemaRecord != null) {
                tipoAcuerdos = tipoAcuerdoRepository.findByMateriaIdAndTipoSistemaId(materiaRecord.id(), tipoSistemaRecord.id());
            } else {
                throw new NotFoundException("Tipo de sistema no encontrado para materia 'FAMILIAR'", carpetaId.toString());
            }
        } else {
            if (materiaRecord != null) {
                tipoAcuerdos = tipoAcuerdoRepository.findByMateriaId(materiaRecord.id());
            }
        }
        tipoAcuerdos = Optional.ofNullable(tipoAcuerdos).orElse(List.of());

        return tipoAcuerdos.stream()
                .map(tipoAcuerdo -> new TipoAcuerdoRecord(tipoAcuerdo.getId(), tipoAcuerdo.getNombre()))
                .toList();
    }

    public List<TipoAcuerdoRecord> findTiposAcuerdoParaFiltro(Integer juzgadoId, Integer materiaId){
        if(juzgadoId == null && materiaId == null){
            return Collections.emptyList();
        }
        Optional<Juzgado> juzgadoOpt = juzgadoRepository.findById(juzgadoId);

        if (juzgadoOpt.isEmpty()) {
            return Collections.emptyList();
        }
        Juzgado juzgado = juzgadoOpt.get();
        List<TipoJuicio> tipoJuiciosAsociados = juzgado.getTipoJuicios();
        if (tipoJuiciosAsociados == null || tipoJuiciosAsociados.isEmpty()) {
            return Collections.emptyList();
        }
        Integer tipoSistemaId = null;
        if(materiaId == 250) {
            TipoJuicio primerTipoJuicio = tipoJuiciosAsociados.get(0);
            tipoSistemaId = primerTipoJuicio.getTipoSistema().getId();
        }
        List<TipoAcuerdo> acuerdos = tipoAcuerdoRepository.findByMateriaIdAndTipoSistemaId(materiaId, tipoSistemaId);

        return acuerdos.stream()
                .map(acuerdo -> new TipoAcuerdoRecord(
                        acuerdo.getId(),
                        acuerdo.getNombre()
                ))
                .toList();
    }
}
