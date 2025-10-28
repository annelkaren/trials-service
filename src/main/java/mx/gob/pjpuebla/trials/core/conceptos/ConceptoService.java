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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
      Carpeta carpeta = carpetaRepository.findById(carpetaId).orElseThrow(() -> new NotFoundException("Carpeta no encontrada", "CarpetaId"+carpetaId));

      return conceptoRepository.findAllByTipoJuicio_IdOrNombreIn(carpeta.getTipoJuicio().getId(), List.of("Adjuntar", "Distribución", "RESGUARDO")).stream()
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
                .orElseThrow(() -> new NotFoundException("Concepto no encontrado", "conceptoId"));
        return new ConceptoRecordResponse(concepto.getId(), concepto.getNombre(), concepto.getDias(), concepto.getEstado(), concepto.getRoles());
    }

    public Page<ConceptoRecord> getAllConceptos(Pageable pageable, String key){
        key = (key != null) ? key.toLowerCase() : "";
        List<Estado> status = SearchLikeEnum.searchByEstadoEnum(key);
        if (status.isEmpty()) {
            status = Arrays.asList(Estado.ACTIVE, Estado.INACTIVE);
        } else {
            key = "";
        }
        Page<Concepto> page = conceptoRepository.findAllConceptos(key, status, pageable);
        List<ConceptoRecord> list = page.stream()
                .map(c -> new ConceptoRecord(
                        c.getId(),
                        c.getNombre() ,
                        c.getDias(),
                        (c.getTipoJuicio() != null ? c.getTipoJuicio().getNombre() : ""),
                        c.getEstado()
                ))
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }



    public ConceptoRecord createConcepto(Concepto concepto) {
        concepto.setNombre(concepto.getNombre());
        concepto.setDias(concepto.getDias());
        concepto.setEstado(concepto.getEstado());

        if (concepto.getTipoJuicio() != null) {
            TipoJuicio tipoJuicio = tipoJuicioRepository.findById(concepto.getTipoJuicio().getId())
                    .orElseThrow(() -> new NotFoundException("TipoJuicio no encontrado", "tipoJuicio"));
            concepto.setTipoJuicio(tipoJuicio);
        }

        Concepto savedConcepto = conceptoRepository.save(concepto);
        return new ConceptoRecord(
                savedConcepto.getId(),
                savedConcepto.getNombre(),
                savedConcepto.getDias(),
                savedConcepto.getTipoJuicio() != null ? savedConcepto.getTipoJuicio().getNombre() : null,
                savedConcepto.getEstado()
        );
    }

    public void delete(Integer id ){
       try {
           conceptoRepository.deleteById(id);
       }catch (DataIntegrityViolationException ex) {
           throw new ConstraintViolationException(Messages.CONSTRAINT_ERROR, "conceptoId" + id);
       }
    }

    public ConceptoRecord updateStatus(Integer id, Integer status){
        Estado estado = Estado.values()[status];
        Concepto concepto = conceptoRepository.findById(id)
                .orElseThrow(()-> new NotFoundException(CONCEPTO_NOT_FOUND, id.toString()));
        concepto.setEstado(estado);
        conceptoRepository.save(concepto);

        return new ConceptoRecord(id, concepto.getNombre(), concepto.getDias(),
                (concepto.getTipoJuicio() != null ? concepto.getTipoJuicio().getNombre() : ""), concepto.getEstado());
    }

    public ConceptoRecord updateConcepto(Concepto updatedConcepto) {
        Optional<Concepto> optionalExistingConcepto = conceptoRepository.findById(updatedConcepto.getId());
        if (optionalExistingConcepto.isEmpty()) {
            throw new EntityNotFoundException("El concepto con ID " + updatedConcepto.getId() + " no se encontró.");
        }

        Concepto existingConcepto = optionalExistingConcepto.get();
        existingConcepto.setNombre(updatedConcepto.getNombre());
        existingConcepto.setDias(updatedConcepto.getDias());
        existingConcepto.setEstado(updatedConcepto.getEstado());

        if (updatedConcepto.getTipoJuicio().getId() != null) {
            Optional<TipoJuicio> optionalTipoJuicio = tipoJuicioRepository.findById(updatedConcepto.getTipoJuicio().getId());
            if (optionalTipoJuicio.isPresent()) {
                existingConcepto.setTipoJuicio(optionalTipoJuicio.get());
            } else {
                throw new EntityNotFoundException("El tipo de juicio con ID " + updatedConcepto.getTipoJuicio().getId() + " no se encontró.");
            }
        } else {
            existingConcepto.setTipoJuicio(null);
        }

        Concepto savedConcepto = conceptoRepository.save(existingConcepto);
        return new ConceptoRecord(
                savedConcepto.getId(),
                savedConcepto.getNombre(),
                savedConcepto.getDias(),
                (savedConcepto.getTipoJuicio() != null ? savedConcepto.getTipoJuicio().getNombre() : ""),
                savedConcepto.getEstado()
        );
    }

    public ConceptoRecordJuicio findByConceptoById(Integer id){
        Concepto concepto = conceptoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Concepto no encontrado", "conceptoId"));
        String tipoJuicioNombre = null;
        Integer tipoJuicioId = null;
        Integer materiaId = null;

        if(concepto.getTipoJuicio() != null) {
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

    public Optional<Concepto> findByNombreAndTipoJuicio(String nombre, TipoJuicio tipoJuicio){
        return conceptoRepository.findByNombreAndTipoJuicio(nombre, tipoJuicio);
    }

    public Optional<Concepto> findByNombre(String nombre){
        return conceptoRepository.findByNombre(nombre);
    }

}
