package mx.gob.pjpuebla.trials.core.materialpericial;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialPericialService {

    private final MaterialPericialRepository materialPericialRepository;


    public List<MaterialPericial> getallMaterialParicial(String nombre) {
        nombre = (nombre != null) ? nombre.toLowerCase() : "";
        return materialPericialRepository.getAllMateriaPericial(nombre);
    }
}
