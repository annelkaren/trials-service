package mx.gob.pjpuebla.trials.core.instituciones;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.core.catalogos.sedes.Sede;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
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
        List<Institucion> instituciones = institucionRepository.findAll();

        String nombreFiltro = example.getNombre() != null ? StringUtils.stripAccents(example.getNombre()).toLowerCase() : "";

        List<Institucion> filtradas = instituciones.stream()
                .filter(inst -> nombreFiltro.isEmpty() || StringUtils.stripAccents(inst.getNombre())
                                .toLowerCase()
                                .contains(nombreFiltro))
                .toList();

        List<InstitucionRecord> records = filtradas.stream()
                .map(inst -> new InstitucionRecord(
                        inst.getId(),
                        inst.getNombre(),
                        inst.getDomicilio().getDireccionInstitucion(),
                        inst.getTelefono(),
                        inst.getTipoInstitucion())
                )
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());

        return new PageImpl<>(records, pageable, filtradas.size());
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

        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        example.setEstado(Estado.ACTIVE);

        Page<Institucion> page = institucionRepository.findAll(Example.of(example, exampleMatcher), pageable);
        return getPageInstitucion(page, pageable);
    }

    private Page<InstitucionRecord> getPageInstitucion(Page<Institucion> page, Pageable pageable) {
        List<InstitucionRecord> list = page.getContent().stream()
                .map(institucion -> new InstitucionRecord(
                        institucion.getId(),
                        institucion.getNombre(),
                        String.join(" ",
                                institucion.getDomicilio().getCalle(),
                                institucion.getDomicilio().getColonia(),
                                institucion.getDomicilio().getExterior(),
                                (institucion.getDomicilio().getInterior() != null && !institucion.getDomicilio().getInterior().isEmpty()) ? "Int. " + institucion.getDomicilio().getInterior() : "",
                                institucion.getDomicilio().getEstadoRepublica(),
                                institucion.getDomicilio().getMunicipio(),
                                institucion.getDomicilio().getLocalidad(),
                                institucion.getDomicilio().getCodigoPostal(),
                                (institucion.getDomicilio().getReferencia() != null && !institucion.getDomicilio().getReferencia().isEmpty()) ? "Ref: " + institucion.getDomicilio().getReferencia() : ""
                        ).trim(),
                        institucion.getTelefono(),
                        institucion.getTipoInstitucion()
                        ))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public List<InstitucionRecord> findByTipoInstitucion(String tipo){
        return institucionRepository.findByTipoInstitucion(tipo);
    }

}
