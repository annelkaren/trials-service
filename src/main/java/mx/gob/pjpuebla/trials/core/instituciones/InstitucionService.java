package mx.gob.pjpuebla.trials.core.instituciones;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class InstitucionService {
    
    private final InstitucionRepository institucionRepository;
    private final DomicilioRepository domicilioRepository;

    @Transactional(readOnly = true)
    public Page<InstitucionRecord> getAll(Institucion example, Pageable pageable) {
        
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        
        // define estados
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        

        // Obtén la página de Institucion con la dirección completa
        Page<InstitucionRecord> page = institucionRepository.findAllEstadoIn(estados, pageable);
    
        // Mapea la lista de Institucion a InstitucionRecord
        List<InstitucionRecord> list = page.getContent().stream()
                .map(institucion -> new InstitucionRecord(
                        institucion.id(),
                        institucion.nombre(),
                        institucion.domicilio(), 
                        institucion.telefono()))
                .toList();
    
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }
    

    @Transactional(readOnly = true)
    public InstitucionRecordResponse findById(Integer id){
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);

        return institucionRepository.findByIdAndEstadoIn(id, estados)
                .orElseThrow(() -> new NotFoundException("Institución no encontrada", "institucionId"));
    }

    public Integer create(Institucion institucion){
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
           throw new OptimisticLockingFailureException("Institución modificada por otro usuario", "institucionId");
        }
    }

}
