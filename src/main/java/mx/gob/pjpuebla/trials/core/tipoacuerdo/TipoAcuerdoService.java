package mx.gob.pjpuebla.trials.core.tipoacuerdo;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.materias.MateriaRecord;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRecord;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Transactional
@RequiredArgsConstructor
@Service
public class TipoAcuerdoService {

    private final TipoAcuerdoRepository tipoAcuerdoRepository;
    private final DocumentoRepository documentoRepository;
    private static final String MATERIA = "FAMILIAR";

    public List<TipoAcuerdoRecord> findByDocumentoId(Integer documentoId) {

        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new NotFoundException("Documento no encontrado", documentoId.toString()));

        MateriaRecord materiaRecord = null;
        TipoSistemaRecord tipoSistemaRecord = null;

        Integer documentoMateriaId = documento.getCarpeta().getTipoJuicio().getMateria().getId();
        String documentoNombre = documento.getCarpeta().getTipoJuicio().getMateria().getNombre();

        if (documentoMateriaId != null && documentoNombre != null) {
            materiaRecord = new MateriaRecord(documentoMateriaId, documentoNombre);
        }

        Integer documentoTipoSistemaId = documento.getCarpeta().getTipoJuicio().getTipoSistema() != null ?
                documento.getCarpeta().getTipoJuicio().getTipoSistema().getId() : null;

        String documentoTipoSistemaNombre = documento.getCarpeta().getTipoJuicio().getTipoSistema() != null ?
                documento.getCarpeta().getTipoJuicio().getTipoSistema().getNombre() : null;

        if (documentoTipoSistemaId != null && documentoTipoSistemaNombre != null) {
            tipoSistemaRecord = new TipoSistemaRecord(documentoTipoSistemaId, documentoTipoSistemaNombre);
        }
        List<TipoAcuerdo> tipoAcuerdos = null;

        if (materiaRecord != null && materiaRecord.nombre().equalsIgnoreCase(MATERIA)) {
            if (tipoSistemaRecord != null) {
                tipoAcuerdos = tipoAcuerdoRepository.findByMateriaIdAndTipoSistemaId(materiaRecord.id(), tipoSistemaRecord.id());
            } else {
                throw new NotFoundException("Tipo de sistema no encontrado para materia 'FAMILIAR'", documentoId.toString());
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
