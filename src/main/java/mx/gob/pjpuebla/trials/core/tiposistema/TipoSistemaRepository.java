package mx.gob.pjpuebla.trials.core.tiposistema;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoSistemaRepository extends JpaRepository<TipoSistema, Integer> {

    Optional<TipoSistema> findByNombre(String nombre);
}