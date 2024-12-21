package mx.gob.pjpuebla.trials.core.materiapericial;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MateriaPericialService {

    private final MateriaPericialRepository materialPericialRepository;


    public List<MateriaPericial> getallMateriaParicial(String nombre) {
        nombre = (nombre != null) ? nombre.toLowerCase() : "";
        return materialPericialRepository.getAllMateriaPericial(nombre);
    }
}
