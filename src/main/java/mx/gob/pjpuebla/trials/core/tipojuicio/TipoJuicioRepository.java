package mx.gob.pjpuebla.trials.core.tipojuicio;

import mx.gob.pjpuebla.trials.util.enums.Estado;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoJuicioRepository extends JpaRepository<TipoJuicio, Integer> {

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioDemandasRecord(t.id, t.nombre)
            FROM TipoJuicio t
            WHERE t.estado = Estado.ACTIVE AND t.tipoSistema.nombre = :nombreSistema AND t.materia.nombre = :nombreMateria
            """)
    List<TipoJuicioDemandasRecord> findByAllTipoJuicios(String nombreSistema, String nombreMateria);

    Optional<TipoJuicio> findByIdAndEstado(Integer integer, Estado estado);

}