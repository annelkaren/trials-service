package mx.gob.pjpuebla.migracion.materias;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MateriasService {
    private final MateriasMigracionRepository materiasMigracionRepository;

    //find all:
    public List<MateriasMigracion> getAllMaterias() {
        return materiasMigracionRepository.findAll();
    }
}
