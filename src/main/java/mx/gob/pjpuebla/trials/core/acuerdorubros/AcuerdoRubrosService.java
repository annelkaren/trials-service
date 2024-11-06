package mx.gob.pjpuebla.trials.core.acuerdorubros;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRecord;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRecord;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AcuerdoRubrosService {
    private final AcuerdoRubrosRepository acuerdoRubrosRepository;
    private final DocumentoRepository documentoRepository;

    @Transactional(readOnly = true)
    public Page<AcuerdoRubrosRecord> getAll(Pageable pageable) {
        Page<AcuerdoRubros> page = acuerdoRubrosRepository.findAll(pageable);
        List<AcuerdoRubrosRecord> list = page.getContent().stream()
                .map(acuerdoRubros -> new AcuerdoRubrosRecord(
                        acuerdoRubros.getId(),
                        acuerdoRubros.getNombre(),
                        new MateriaRecord(acuerdoRubros.getMateria().getId(), acuerdoRubros.getMateria().getNombre()),
                        acuerdoRubros.getTipoSistema() != null ? new TipoSistemaRecord(acuerdoRubros.getTipoSistema().getId(), acuerdoRubros.getTipoSistema().getNombre()) : null
                ))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public AcuerdoRubrosRecord findById(Integer id) {
        AcuerdoRubros acuerdoRubros = acuerdoRubrosRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Acuerdo rubro no encontrado", "acuerdoRubroId" + id));
        return new AcuerdoRubrosRecord(
                acuerdoRubros.getId(),
                acuerdoRubros.getNombre(),
                new MateriaRecord(acuerdoRubros.getMateria().getId(), acuerdoRubros.getMateria().getNombre()),
                acuerdoRubros.getTipoSistema() != null ? new TipoSistemaRecord(acuerdoRubros.getTipoSistema().getId(), acuerdoRubros.getTipoSistema().getNombre()) : null
        );
    }

    @Transactional(readOnly = true)
    public Page<AcuerdoRubrosRecord> findRubrosByDocumentoId(Integer id, Pageable pageable) {
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

        Page<AcuerdoRubros> acuerdoRubrosList;

        if ("FAMILIAR".equalsIgnoreCase(materia.getNombre())) {
            if (tipoSistema != null) {
                acuerdoRubrosList = acuerdoRubrosRepository.findByMateriaAndTipoSistema(materia, tipoSistema, pageable);
            } else {
                acuerdoRubrosList = acuerdoRubrosRepository.findByMateria(materia, pageable);
            }
        } else {
            acuerdoRubrosList = acuerdoRubrosRepository.findByMateria(materia, pageable);
        }

        return acuerdoRubrosList.map(acuerdoRubros -> new AcuerdoRubrosRecord(
                acuerdoRubros.getId(),
                acuerdoRubros.getNombre(),
                new MateriaRecord(acuerdoRubros.getMateria().getId(), acuerdoRubros.getMateria().getNombre()),
                acuerdoRubros.getTipoSistema() != null ? new TipoSistemaRecord(acuerdoRubros.getTipoSistema().getId(), acuerdoRubros.getTipoSistema().getNombre()) : null
        ));
    }
}
