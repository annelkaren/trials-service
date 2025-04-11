package mx.gob.pjpuebla.trials.core.instituciones;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.instituciones.records.InstitucionRecord;
import mx.gob.pjpuebla.trials.core.instituciones.records.InstitucionRecordResponse;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.error.NotFoundException;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class InstitucionService {

    private final InstitucionRepository institucionRepository;
    private final DomicilioRepository domicilioRepository;

    @Transactional(readOnly = true)
    public Page<InstitucionRecord> getAll(Institucion example, Pageable pageable) {
        // Definir el filtro de nombre si es necesario
        String nombreFiltro = example.getNombre() != null ? StringUtils.stripAccents(example.getNombre()).toLowerCase() : "";
    
        // Llamar al repositorio con la consulta que ya hemos definido, pasando el filtro y el pageable
        return institucionRepository.findAllInstituciones(nombreFiltro, pageable, List.of(Estado.ACTIVE, Estado.INACTIVE));
    }


    @Transactional(readOnly = true)
    public InstitucionRecordResponse findById(Integer id) {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);

        return institucionRepository.findByIdAndEstadoIn(id, estados)
                .orElseThrow(() -> new NotFoundException("Institución no encontrada", "institucionId"));
    }

    public Integer create(Institucion institucion) {
        if(institucionRepository.findByNombre(institucion.getNombre()).isPresent()){
            throw new ConflictException("No pueden existir 2 instituciones con el mismo nombre");
        }

        institucion.setDomicilio(domicilioRepository.save(institucion.getDomicilio()));
        institucion = institucionRepository.save(institucion);
        return institucion.getId();
    }

    public Integer update(Institucion institucion) {
        try {
            institucion.setDomicilio(domicilioRepository.save(institucion.getDomicilio()));
            institucion = institucionRepository.save(institucion);

            return institucion.getId();

        } catch (org.springframework.dao.OptimisticLockingFailureException ex) {
            throw new InvalidVersionException(Sede.class.getSimpleName());
        }
    }

    public void delete(Integer id) {
        institucionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<InstitucionRecord> getAllByEstadoAutocomplete(Institucion example, Pageable pageable) {
        // Llamamos directamente a la consulta del repositorio con el filtro de nombre y estado
        return institucionRepository.findAllInstituciones(
                example.getNombre(),
                pageable,
                List.of(Estado.ACTIVE) // Filtramos solo las instituciones activas
        );
    }

    public List<InstitucionRecord> findByTipoInstitucion(String tipo){
        return institucionRepository.findByTipoInstitucion(tipo);
    }

}
