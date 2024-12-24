package mx.gob.pjpuebla.trials.core.acuerdorubros;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
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
    private final CarpetaRepository carpetaRepository;

    @Transactional(readOnly = true)
    public Page<AcuerdoRubrosRecord> getAll(Pageable pageable) {
        Page<AcuerdoRubros> page = acuerdoRubrosRepository.findAll(pageable);
        List<AcuerdoRubrosRecord> list = page.getContent().stream()
                .map(acuerdoRubros -> new AcuerdoRubrosRecord(
                        acuerdoRubros.getId(),
                        acuerdoRubros.getNombre()
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
                acuerdoRubros.getNombre()
        );
    }

    @Transactional(readOnly = true)
    public Page<AcuerdoRubrosRecord> findRubrosByDocumentoId(Integer carpetaId, Pageable pageable, String nombre) {
        Carpeta carpeta = carpetaRepository.findById(carpetaId)
            .orElseThrow(() -> new NotFoundException("Carpeta no encontrada", "carpetaId" + carpetaId));;
        

        TipoSistema tipoSistema = null;
        Materia materia = null;

        if (carpeta != null && carpeta.getTipoJuicio() != null) {
            tipoSistema = carpeta.getTipoJuicio().getTipoSistema();
            materia = carpeta.getTipoJuicio().getMateria();
        }

        if (materia == null) {
            throw new NotFoundException("Materia no encontrada para la carpeta", "carpetaId" + carpetaId);
        }

        Page<AcuerdoRubros> acuerdoRubrosList;
        nombre = (nombre != null) ? nombre.toLowerCase() : "";

        if ("FAMILIAR".equalsIgnoreCase(materia.getNombre())) {
            if (tipoSistema != null) {
                acuerdoRubrosList = acuerdoRubrosRepository.findByMateriaAndTipoSistemaAndNombreContainingIgnoreCase(materia, tipoSistema, nombre, pageable);
            } else {
                acuerdoRubrosList = acuerdoRubrosRepository.findByMateriaAndNombreContainingIgnoreCase(materia, nombre, pageable);
            }
        } else {
            acuerdoRubrosList = acuerdoRubrosRepository.findByMateriaAndNombreContainingIgnoreCase(materia, nombre, pageable);
        }

        return acuerdoRubrosList.map(acuerdoRubros -> new AcuerdoRubrosRecord(
                acuerdoRubros.getId(),
                acuerdoRubros.getNombre()
        ));
    }
}
