package mx.gob.pjpuebla.trials.core.conceptos;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.error.ConstraintViolationException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.Messages;
import mx.gob.pjpuebla.trials.util.SearchLikeEnum;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ConceptoService {

    private static final String CONCEPTO_NOT_FOUND = "Concepto no encontrado";
    private final ConceptoRepository conceptoRepository;
    private final CarpetaRepository carpetaRepository;
    private final TipoJuicioRepository tipoJuicioRepository;

    @Transactional(readOnly = true)
    public List<ConceptoRecordResponse> getAll(Integer carpetaId) {
        Carpeta carpeta = carpetaRepository.findById(carpetaId)
                .orElseThrow(() -> new NotFoundException("Carpeta no encontrada", "CarpetaId" + carpetaId));

        return conceptoRepository
                .findAllByTipoJuicio_IdOrNombreIn(carpeta.getTipoJuicio().getId(),
                        List.of("Adjuntar", "Distribución", "RESGUARDO"))
                .stream()
                .map(concepto -> new ConceptoRecordResponse(
                        concepto.getId(),
                        concepto.getNombre().toUpperCase(),
                        concepto.getDias(),
                        concepto.getEstado(),
                        concepto.getRoles()))
                .toList();
    }

    @Transactional(readOnly = true)
    public ConceptoRecordResponse findById(Integer id) {
        Concepto concepto = conceptoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CONCEPTO_NOT_FOUND, "conceptoId"));
        return new ConceptoRecordResponse(concepto.getId(), concepto.getNombre(), concepto.getDias(),
                concepto.getEstado(), concepto.getRoles());
    }

    public Page<ConceptoRecord> getAllConceptos(Pageable pageable, String key, String nombre, Integer dias,
            String nombreTipoJuicio, Estado estatus) {

        key = (key != null) ? key.toLowerCase() : "";
        nombre = (nombre != null) ? nombre.toLowerCase() : "";
        nombreTipoJuicio = (nombreTipoJuicio != null) ? nombreTipoJuicio.toLowerCase() : "";
        dias = (dias != null) ? dias : null;

        List<Estado> estados = estatus != null ? List.of(estatus) : Arrays.asList(Estado.ACTIVE, Estado.INACTIVE);

        Page<Concepto> page = conceptoRepository.findAllConceptos(key, estados, pageable, nombre, dias,
                nombreTipoJuicio);

        List<ConceptoRecord> list = page.stream()
                .map(c -> new ConceptoRecord(
                        c.getId(),
                        c.getNombre(),
                        c.getDias(),
                        c.getTipoJuicio() != null ? c.getTipoJuicio().getNombre() : "",
                        c.getEstado()))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public List<ConceptoRecord> createConcepto(ConceptoBulkRequest conceptoRequest) {
        Set<Integer> tipoJuicioIds = extractTipoJuicioIds(conceptoRequest.tipoJuicios(), false);
        List<TipoJuicio> tipoJuicios = tipoJuicioRepository.findAllById(tipoJuicioIds);
        if (tipoJuicios.size() != tipoJuicioIds.size()) {
            throw new NotFoundException("TipoJuicio no encontrado", "tipoJuicios");
        }

        for (TipoJuicio tipoJuicio : tipoJuicios) {
            if (conceptoRepository.findByNombreAndTipoJuicio(conceptoRequest.nombre(), tipoJuicio).isPresent()) {
                throw new ConstraintViolationException(
                        "Ya existe un concepto con el mismo nombre para el tipo de juicio seleccionado", "tipoJuicios");
            }
        }

        return tipoJuicios.stream()
                .map(tipoJuicio -> {
                    Concepto concepto = new Concepto()
                            .setNombre(conceptoRequest.nombre())
                            .setDias(conceptoRequest.dias())
                            .setEstado(conceptoRequest.estado())
                            .setTipoJuicio(tipoJuicio);
                    Concepto savedConcepto = conceptoRepository.save(concepto);
                    return new ConceptoRecord(
                            savedConcepto.getId(),
                            savedConcepto.getNombre(),
                            savedConcepto.getDias(),
                            savedConcepto.getTipoJuicio() != null ? savedConcepto.getTipoJuicio().getNombre() : null,
                            savedConcepto.getEstado());
                })
                .toList();
    }

    public void delete(Integer id) {
        try {
            conceptoRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new ConstraintViolationException(Messages.CONSTRAINT_ERROR, "conceptoId" + id);
        }
    }

    public ConceptoRecord updateStatus(Integer id, Integer status) {
        Estado estado = Estado.values()[status];
        Concepto concepto = conceptoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CONCEPTO_NOT_FOUND, id.toString()));
        concepto.setEstado(estado);
        conceptoRepository.save(concepto);

        return new ConceptoRecord(id, concepto.getNombre(), concepto.getDias(),
                concepto.getTipoJuicio() != null ? concepto.getTipoJuicio().getNombre() : "", concepto.getEstado());
    }

    public ConceptoRecord updateConcepto(ConceptoBulkRequest updatedConcepto) {
        if (updatedConcepto == null || updatedConcepto.id() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id del concepto es obligatorio");
        }
        Set<Integer> tipoJuicioIds = extractTipoJuicioIds(updatedConcepto.tipoJuicios(), true);
        Integer tipoJuicioId = tipoJuicioIds.iterator().next();

        Concepto existingConcepto = conceptoRepository.findById(updatedConcepto.id())
                .orElseThrow(() -> new EntityNotFoundException(
                        "El concepto con ID " + updatedConcepto.id() + " no se encontró."));
        TipoJuicio tipoJuicio = tipoJuicioRepository.findById(tipoJuicioId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "El tipo de juicio con ID " + tipoJuicioId + " no se encontró."));

        existingConcepto.setNombre(updatedConcepto.nombre());
        existingConcepto.setDias(updatedConcepto.dias());
        existingConcepto.setEstado(updatedConcepto.estado());
        existingConcepto.setTipoJuicio(tipoJuicio);

        Optional<Concepto> duplicateConcept = conceptoRepository.findByNombreAndTipoJuicio(
                existingConcepto.getNombre(), existingConcepto.getTipoJuicio());
        if (duplicateConcept.isPresent() && !duplicateConcept.get().getId().equals(existingConcepto.getId())) {
            throw new ConstraintViolationException(
                    "Ya existe un concepto con el mismo nombre para el tipo de juicio seleccionado", "tipoJuicios");
        }

        Concepto savedConcepto = conceptoRepository.save(existingConcepto);
        return new ConceptoRecord(
                savedConcepto.getId(),
                savedConcepto.getNombre(),
                savedConcepto.getDias(),
                savedConcepto.getTipoJuicio() != null ? savedConcepto.getTipoJuicio().getNombre() : "",
                savedConcepto.getEstado());
    }

    public ConceptoRecordJuicio findByConceptoById(Integer id) {
        Concepto concepto = conceptoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CONCEPTO_NOT_FOUND, "conceptoId"));
        String tipoJuicioNombre = null;
        Integer tipoJuicioId = null;
        Integer materiaId = null;

        if (concepto.getTipoJuicio() != null) {
            tipoJuicioNombre = concepto.getTipoJuicio().getNombre();
            tipoJuicioId = concepto.getTipoJuicio().getId();
            materiaId = concepto.getTipoJuicio().getMateria().getId();
        }
        return new ConceptoRecordJuicio(
                concepto.getId(),
                concepto.getNombre(),
                tipoJuicioNombre,
                concepto.getDias(),
                concepto.getEstado(),
                tipoJuicioId,
                materiaId);
    }

    public Optional<Concepto> findByNombreAndTipoJuicio(String nombre, TipoJuicio tipoJuicio) {
        return conceptoRepository.findByNombreAndTipoJuicio(nombre, tipoJuicio);
    }

    public Optional<Concepto> findByNombre(String nombre) {
        return conceptoRepository.findByNombre(nombre);
    }

    private Set<Integer> extractTipoJuicioIds(List<TipoJuicioIdRequest> tipoJuicios, boolean onlyOne) {
        if (tipoJuicios == null || tipoJuicios.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe seleccionar al menos un tipo de juicio");
        }

        Set<Integer> tipoJuicioIds = new LinkedHashSet<>(tipoJuicios.stream()
                .map(TipoJuicioIdRequest::id)
                .filter(id -> id != null)
                .toList());
        if (tipoJuicioIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe seleccionar al menos un tipo de juicio");
        }
        if (onlyOne && tipoJuicioIds.size() > 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se permite actualizar un tipo de juicio");
        }
        return tipoJuicioIds;
    }
}
