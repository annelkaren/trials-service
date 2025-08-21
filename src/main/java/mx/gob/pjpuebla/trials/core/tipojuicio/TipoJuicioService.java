package mx.gob.pjpuebla.trials.core.tipojuicio;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.materias.MateriaRecord;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRecord;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TipoJuicioService {

    private final TipoJuicioRepository tipoJuicioRepository;
    private final PersonaService personaService;

    @Transactional(readOnly = true)
    public Page<TipoJuicioRecord> getAllActive(Pageable pageable, TipoJuicio example) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase())
                .withMatcher("tipoSistema.nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("materia.nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<TipoJuicio> page = tipoJuicioRepository.findAll(Example.of(example.setEstado(Estado.ACTIVE), exampleMatcher), pageable);
        List<TipoJuicioRecord> list = page.getContent().stream()
                .map(m -> new TipoJuicioRecord(m.getId(), m.getNombre(), new TipoSistemaRecord(m.getTipoSistema().getId(), m.getTipoSistema().getNombre()), new MateriaRecord(m.getMateria().getId(), m.getMateria().getNombre())))
                .toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional
    public List<TipoJuicioDemandasRecord> getAllTipoJuicios() {
        List<TipoJuicioDemandasRecord> results = tipoJuicioRepository.findByAllTipoJuicios("Oral", "FAMILIAR");
        if (results.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron tipos de juicio para Oral y FAMILIAR");
        }
        return results;
    }

    @Transactional(readOnly = true)
    public TipoJuicioRecord findById(Integer id) {
        TipoJuicio tipoJuicio = tipoJuicioRepository.findByIdAndEstado(id, Estado.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Tipo de Juicio no encontrado", "tipoJuicioId"));
        return new TipoJuicioRecord(tipoJuicio.getId(), tipoJuicio.getNombre(), new TipoSistemaRecord(tipoJuicio.getTipoSistema().getId(), tipoJuicio.getTipoSistema().getNombre()), new MateriaRecord(tipoJuicio.getMateria().getId(), tipoJuicio.getMateria().getNombre()));
    }

    @Transactional(readOnly = true)
    public Page<TipoJuicioRecord> getAllActiveByCentroTrabajo(Pageable pageable) {
        Persona usuario = personaService.getAuditor();
        
        Integer centroTrabajoId = usuario.getOficialia() != null ? usuario.getOficialia().getId() : usuario.getJuzgado().getId();

        if (centroTrabajoId == null) {
            throw new NotFoundException("No se pudo obtener el Centro de Trabajo", "Centro de Trabajo");
        }

        List<TipoJuicioRecord> list = new ArrayList<>();
        Page<TipoJuicio> page = tipoJuicioRepository.findByCentroTrabajo(
                usuario.getOficialia() != null ? usuario.getOficialia().getId() : null,
                usuario.getJuzgado() != null ? usuario.getJuzgado().getId() : null, pageable);
                
        for (TipoJuicio tipoJuicio : page.getContent()) {
            if (!tipoJuicio.getMateria().getNombre().equalsIgnoreCase("exhorto")) {
                TipoJuicioRecord tipoJuicioRecord = new TipoJuicioRecord(tipoJuicio.getId(), tipoJuicio.getNombre(),
                        new TipoSistemaRecord(tipoJuicio.getTipoSistema().getId(), tipoJuicio.getTipoSistema().getNombre()),
                        new MateriaRecord(
                                tipoJuicio.getMateria().getId(),
                                StringUtils.capitalize(tipoJuicio.getMateria().getNombre().toLowerCase())));
                list.add(tipoJuicioRecord);
            }
        }
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public List<TipoJuicioMateriaRecord> findTipoJuiciosByMateria(Integer materiaId) {
        List<TipoJuicio> tipoJuicios = tipoJuicioRepository.findByMateriaId(materiaId);
        return tipoJuicios.stream()
                .map(tj -> new TipoJuicioMateriaRecord(tj.getId(), tj.getNombre(), materiaId))
                .toList();
    }

    public List<TipoJuicioDemandasRecord> getAllTipoJuicioHijo(Integer tipoJuicioPadreId) {
        List<TipoJuicioDemandasRecord> result = tipoJuicioRepository.findByTipoJuicioPadre(tipoJuicioPadreId);

        if (result.isEmpty()) {
            throw new NotFoundException("No hay Juicios asociados", "tipoJuicioPadreId");
        }

        return result;
    }

    public TipoJuicio findByNombre(String nombre){
        Optional<TipoJuicio> tipoJuicio = tipoJuicioRepository.findByNombreIgnoreCase(nombre);

        if(tipoJuicio.isPresent()){
            return tipoJuicio.get();
        }

        return null;
    }
}
