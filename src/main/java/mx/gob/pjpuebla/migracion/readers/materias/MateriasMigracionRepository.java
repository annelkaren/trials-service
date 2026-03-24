package mx.gob.pjpuebla.migracion.readers.materias;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MateriasMigracionRepository extends JpaRepository<MateriasMigracion, Integer> {

    Optional<MateriasMigracion> findByCodigo(String codigo);

}
