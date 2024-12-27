
package mx.gob.pjpuebla.trials.core.tipoprueba;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoPruebasRepository extends JpaRepository<TipoPruebas, Integer> {
    Optional<TipoPruebas> findByNombre(String nombre);
}