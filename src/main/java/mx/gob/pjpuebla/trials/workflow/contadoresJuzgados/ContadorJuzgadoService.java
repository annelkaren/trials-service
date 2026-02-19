package mx.gob.pjpuebla.trials.workflow.contadoresJuzgados;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.error.ConflictException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;

@Service
@RequiredArgsConstructor
@Transactional
public class ContadorJuzgadoService {

    private final ContadorJuzgadoRepository contadorJuzgadoRepository;
    private final JuzgadoRepository juzgadoRepository;
    private final TipoJuicioRepository tipoJuicioRepository;

    @Transactional(readOnly = true)
    public Page<ContadorJuzgadoResponseRecord> getAll(Pageable pageable) {
        return contadorJuzgadoRepository.findByEstado(Estado.ACTIVE, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ContadorJuzgadoResponseRecord getById(Integer id) {
        return toResponse(findEntityById(id, List.of(Estado.ACTIVE, Estado.INACTIVE)));
    }

    public ContadorJuzgadoResponseRecord create(ContadorJuzgadoSaveRecord record) {
        ContadorJuzgado existing = contadorJuzgadoRepository
                .findByJuzgadoIdAndTipoJuicioId(record.juzgadoId(), record.tipoJuicioId())
                .orElse(null);
        if (existing != null) {
            fillEntityFromRecord(existing, record);
            existing.setEstado(Estado.ACTIVE);
            return toResponse(contadorJuzgadoRepository.save(existing));
        }
        ContadorJuzgado contador = new ContadorJuzgado();
        fillEntityFromRecord(contador, record);
        contador.setEstado(Estado.ACTIVE);
        return toResponse(contadorJuzgadoRepository.save(contador));
    }

    public ContadorJuzgadoResponseRecord update(Integer id, ContadorJuzgadoSaveRecord record) {
        ContadorJuzgado contador = findEntityById(id, List.of(Estado.ACTIVE, Estado.INACTIVE));
        validateUniquePair(record.juzgadoId(), record.tipoJuicioId(), id);
        fillEntityFromRecord(contador, record);
        contador.setEstado(Estado.ACTIVE);
        return toResponse(contadorJuzgadoRepository.save(contador));
    }

    public void delete(Integer id) {
        ContadorJuzgado contador = findEntityById(id, List.of(Estado.ACTIVE, Estado.INACTIVE));
        contador.setEstado(Estado.INACTIVE);
        contadorJuzgadoRepository.save(contador);
    }

    private void fillEntityFromRecord(ContadorJuzgado contador, ContadorJuzgadoSaveRecord record) {
        Juzgado juzgado = juzgadoRepository.findById(record.juzgadoId())
                .orElseThrow(() -> new NotFoundException("Juzgado no encontrado", "juzgadoId"));
        TipoJuicio tipoJuicio = tipoJuicioRepository.findById(record.tipoJuicioId())
                .orElseThrow(() -> new NotFoundException("Tipo de juicio no encontrado", "tipoJuicioId"));

        contador.setJuzgado(juzgado);
        contador.setTipoJuicio(tipoJuicio);
        contador.setMaxAsignaciones(record.maxAsignaciones());
        contador.setContadorAsignaciones(record.contadorAsignaciones() == null ? 0 : record.contadorAsignaciones());
    }

    private void validateUniquePair(Integer juzgadoId, Integer tipoJuicioId, Integer currentId) {
        contadorJuzgadoRepository.findByJuzgadoIdAndTipoJuicioId(juzgadoId, tipoJuicioId)
                .ifPresent(existing -> {
                    if (currentId == null || !existing.getId().equals(currentId)) {
                        throw new ConflictException("Ya existe un contador para el juzgado y tipo de juicio seleccionados");
                    }
                });
    }

    private ContadorJuzgado findEntityById(Integer id, List<Estado> estados) {
        return contadorJuzgadoRepository.findByIdAndEstadoIn(id, estados)
                .orElseThrow(() -> new NotFoundException("Contador de juzgado no encontrado", "id"));
    }

    private ContadorJuzgadoResponseRecord toResponse(ContadorJuzgado contador) {
        return new ContadorJuzgadoResponseRecord(
                contador.getId(),
                contador.getJuzgado().getId(),
                contador.getJuzgado().getNombre(),
                contador.getTipoJuicio().getId(),
                contador.getTipoJuicio().getNombre(),
                contador.getMaxAsignaciones(),
                contador.getContadorAsignaciones(),
                contador.getEstado());
    }
}
