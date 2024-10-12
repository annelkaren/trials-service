package mx.gob.pjpuebla.trials.workflow.tipojuicioetiquetas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoJuicioEtiquetaRepository extends JpaRepository<TipoJuicioEtiqueta, Integer> {

    List<TipoJuicioEtiqueta> findByTipoJuicioId(Integer id);

    @Query("""
            SELECT te.value
            FROM TipoJuicioEtiqueta te
            WHERE te.tipoJuicio.id = :tipoJuicioId
            AND lower(te.nombre) = lower(:nombreEtiqueta)
            """)
    String getEtiquetaByNombreAndTipoJuicio(@Param("tipoJuicioId") Integer tipoJuicioId,@Param("nombreEtiqueta") String nombreEtiqueta);
}
