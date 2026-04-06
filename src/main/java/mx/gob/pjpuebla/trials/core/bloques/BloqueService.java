package mx.gob.pjpuebla.trials.core.bloques;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BloqueService {
    private final BloqueRepository bloqueRepository;

    @Transactional(readOnly = true)
    public Page<BloqueRecordResponse> getAll(String key, Pageable pageable) {
        key = key != null ? key : "";
        return bloqueRepository.findAllByKey(key, pageable);
    }

    @Transactional(readOnly = true)
    public BloqueRecordResponse findById(Integer id) {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        return bloqueRepository.findByIdAndEstadoIn(id, estados)
                .orElseThrow(() -> new NotFoundException("Bloque no encontrado", "bloqueId"));
    }

    public BloqueRecordResponse create(Bloque bloque) {
        bloque = bloqueRepository.save(bloque);
        return new BloqueRecordResponse(bloque.getId(), bloque.getHoraInicial(), bloque.getHoraFinal(), bloque.getEstado());
    }

    public BloqueRecordResponse update(Bloque bloque) {
        try {
            bloqueRepository.save(bloque);
            return new BloqueRecordResponse(bloque.getId(), bloque.getHoraInicial(), bloque.getHoraFinal(), bloque.getEstado());
        } catch (org.springframework.dao.OptimisticLockingFailureException ex) {
            throw new InvalidVersionException(Bloque.class.getSimpleName());
        }
    }

    public void delete(Integer id) {
        bloqueRepository.deleteById(id);
    }

    public List<LocalTime> getCitas(Bloque bloque){
        List<LocalTime> citas = new ArrayList<>();
        LocalTime cita = bloque.getHoraInicial();

        citas.add(cita);

        while(cita.isBefore(bloque.getHoraFinal())){
            cita = cita.plusMinutes(30);

            citas.add(cita);
        }

        return citas;
        
    }

}
