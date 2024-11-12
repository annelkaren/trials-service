package mx.gob.pjpuebla.trials.core.tipoaudiencia;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class TipoAudienciaService {

    private final TipoAudienciaRepository tipoAudienciaRepository;
    private final DocumentoRepository documentoRepository;

    @Transactional(readOnly = true)
    public Page<TipoAudienciaRecord> getAll(Pageable pageable) {
        Page<TipoAudiencia> page = tipoAudienciaRepository.findAll(pageable);
        List<TipoAudienciaRecord> list = page.getContent().stream()
                .map(tipoAudiencia -> new TipoAudienciaRecord(
                        tipoAudiencia.getId(),
                        tipoAudiencia.getNombre()
                ))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public TipoAudienciaRecord findById(Integer id) {
        TipoAudiencia tipoAudiencia = tipoAudienciaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tipo audiencia no encontrada", "tipoAudiencia" + id));
        return new TipoAudienciaRecord(
                tipoAudiencia.getId(),
                tipoAudiencia.getNombre()
        );
    }

    @Transactional(readOnly = true)
    public Page<TipoAudienciaRecord> findTipoAudienciaByDocumentoId(Integer id, Pageable pageable, String nombre) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Documento no encontrado", "documentoId" + id));

        TipoSistema tipoSistema = null;
        Materia materia = null;

        if (documento.getCarpeta() != null && documento.getCarpeta().getTipoJuicio() != null) {
            tipoSistema = documento.getCarpeta().getTipoJuicio().getTipoSistema();
            materia = documento.getCarpeta().getTipoJuicio().getMateria();
        }

        if (materia == null) {
            throw new NotFoundException("Materia no encontrada para el documento", "documentoId" + id);
        }

        Page<TipoAudiencia> tipoAudienciaList;
        nombre = (nombre != null) ? nombre.toLowerCase() : "";

        if (tipoSistema != null) {
            tipoAudienciaList = tipoAudienciaRepository.findByMateriaAndTipoSistemaAndNombreContainingIgnoreCase(materia, tipoSistema, nombre, pageable);
        } else {
            tipoAudienciaList = tipoAudienciaRepository.findByMateriaAndNombreContainingIgnoreCase(materia, nombre, pageable);
        }

        return tipoAudienciaList.map(tipoAudiencia -> new TipoAudienciaRecord(
                tipoAudiencia.getId(),
                tipoAudiencia.getNombre()
        ));
    }

    public TipoAudiencia obtenerTipoAudiencia(String tipoAudiencia) {
        return tipoAudienciaRepository.findByNombre(tipoAudiencia);
    }
}
