package mx.gob.pjpuebla.trials.core.paises;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Integer> {

    Optional<Pais> findByNombreComun(String nombreComun);

}
