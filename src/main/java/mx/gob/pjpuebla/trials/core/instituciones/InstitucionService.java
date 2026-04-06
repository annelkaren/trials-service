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
    public Page<InstitucionRecord> getAll(String key, String nombre, String direccion, String telefono, Estado estatus,
            Pageable pageable) {

        List<Estado> estados = estatus == null ? List.of(Estado.ACTIVE, Estado.INACTIVE) : List.of(estatus);
        nombre = nombre != null ? nombre : "";
        direccion = direccion != null ? direccion : "";
        telefono = telefono != null ? telefono : "";
        key = key != null ? key : "";

        return institucionRepository.findAllInstituciones(key, nombre, direccion, telefono, estados, pageable);
    }

    @Transactional(readOnly = true)
    public InstitucionRecordResponse findById(Integer id) {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);

        return institucionRepository.findByIdAndEstadoIn(id, estados)
                .orElseThrow(() -> new NotFoundException("Institución no encontrada", "institucionId"));
    }

    public Integer create(Institucion institucion) {
        if (institucionRepository.findByNombre(institucion.getNombre()).isPresent()) {
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
        return institucionRepository.findAllInstituciones(
                "",
                example.getNombre(),
                "",
                "",
                List.of(Estado.ACTIVE),
                pageable);
    }

    public List<InstitucionRecord> findByTipoInstitucion(String tipo) {
        return institucionRepository.findByTipoInstitucion(tipo);
    }

    public List<InstitucionRecord> getAllInstitucionesList() {
        return institucionRepository.getInstitucionesList();
    }

}
