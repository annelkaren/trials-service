
package mx.gob.pjpuebla.trials.core.tipoprueba;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;


@Repository
public interface TipoPruebasRepository extends JpaRepository<TipoPruebas, Integer> {
    Optional<TipoPruebas> findByNombre(String nombre);
    
    List<TipoPruebas> findByTipoJuicio(TipoJuicio tipoJuicio);


}