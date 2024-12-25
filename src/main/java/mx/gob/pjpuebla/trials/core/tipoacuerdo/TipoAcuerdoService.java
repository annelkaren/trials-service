package mx.gob.pjpuebla.trials.core.tipoacuerdo;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.materias.MateriaRecord;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRecord;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Transactional
@RequiredArgsConstructor
@Service
public class TipoAcuerdoService {

    private final TipoAcuerdoRepository tipoAcuerdoRepository;
    private final CarpetaRepository carpetaRepository;
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

}
