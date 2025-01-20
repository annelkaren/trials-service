package mx.gob.pjpuebla.trials.core.tipopartes;

import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoPartesRepository extends JpaRepository<TipoPartes, Integer> {

    Optional<TipoPartes> findByIdAndEstado(Integer id, Estado estado);

    @Query("SELECT tp FROM TipoPartes tp WHERE tp.tipoJuicio.id = :id AND LOWER(tp.nombre) <> 'promovente'")
    List<TipoPartes> findByTipoJuicioIdAndNotPromovente(@Param("id") Integer id);

    Optional<TipoPartes> findByNombreAndTipoJuicioId(String name, Integer tipoJuicioId);

    Optional<TipoPartes> findByNombre(String nombre);
}