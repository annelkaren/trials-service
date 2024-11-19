package mx.gob.pjpuebla.trials.core.rubros;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class RubroService {

    private final RubroRepository rubroRepository;

    @Transactional(readOnly = true)
    public List<RubroRecord> getAllByProcedimiento(Integer procedimientoId) {
        List<Rubro> rubroList = rubroRepository.findByProcedimientoIdAndEstado(procedimientoId, Estado.ACTIVE);
        return rubroList.stream().map(
                p -> new RubroRecord(p.getId(), p.getNombre())
        ).toList();
    }
}
