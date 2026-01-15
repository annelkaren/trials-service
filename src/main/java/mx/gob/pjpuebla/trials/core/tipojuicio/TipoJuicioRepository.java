package mx.gob.pjpuebla.trials.core.tipojuicio;

import mx.gob.pjpuebla.trials.util.enums.Estado;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Optional<TipoJuicio> findByNombreIgnoreCase(String name);

    @Query("""
            SELECT tj FROM TipoJuicio tj
            WHERE  tj.estado = Estado.ACTIVE
            AND CASE WHEN :oficialiaId IS NOT NULL
                THEN
                    (SELECT COUNT(1) FROM Oficialia o where o.id = :oficialiaId
                    AND EXISTS (
                        SELECT j FROM Juzgado j where j.id in (select tmpo.id from o.juzgados tmpo ) and tj.id in (select tmpj.id from j.tipoJuicios tmpj)
                    ))
                WHEN :juzgadoId IS NOT NULL THEN
                    (SELECT COUNT(1) FROM Juzgado jj where jj.id = :juzgadoId and tj.id in (select tmpj.id from jj.tipoJuicios tmpj))
                ELSE
                    0
                END > 0
            AND tj.tipoJuicioPadreOral IS NULL AND
            tj.tipoJuicioPadreTrad IS NULL
            """)
    Page<TipoJuicio> findByCentroTrabajo(Integer oficialiaId, Integer juzgadoId, Pageable pageable);


    
    @Query("""
            SELECT new mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRecord(
            tj.id,
            tj.nombre,
            new mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRecord(tj.tipoSistema.id, tj.tipoSistema.nombre),
            new mx.gob.pjpuebla.trials.core.materias.MateriaRecord(tj.materia.id, INITCAP(LOWER(tj.materia.nombre)))
            )
            FROM TipoJuicio tj
            WHERE tj.estado = Estado.ACTIVE
                            AND CASE WHEN :oficialiaId IS NOT NULL
                THEN
                    (SELECT COUNT(1) FROM Oficialia o where o.id = :oficialiaId
                    AND EXISTS (
                        SELECT j FROM Juzgado j where j.id in (select tmpo.id from o.juzgados tmpo ) and tj.id in (select tmpj.id from j.tipoJuicios tmpj)
                    ))
                WHEN :juzgadoId IS NOT NULL THEN
                    (SELECT COUNT(1) FROM Juzgado jj where jj.id = :juzgadoId and tj.id in (select tmpj.id from jj.tipoJuicios tmpj))
                ELSE
                    0
                END > 0
            AND tj.tipoJuicioPadreOral IS NULL AND
            tj.tipoJuicioPadreTrad IS NULL
            AND INITCAP(LOWER(tj.materia.nombre)) != 'Exhorto'
            """)
    List<TipoJuicioRecord> findTipoJuicioAll(Integer oficialiaId, Integer juzgadoId);


    @Query("SELECT tj FROM TipoJuicio tj WHERE tj.materia.id = :materiaId AND tj.tipoJuicioPadreOral IS NULL AND tj.tipoJuicioPadreTrad IS NULL")
    List<TipoJuicio> findByMateriaId(@Param("materiaId") Integer materiaId);

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioDemandasRecord(t.id, t.nombre)
            FROM TipoJuicio t
            WHERE t.estado = Estado.ACTIVE AND (t.tipoJuicioPadreOral = :tipoJuicioPadreId OR t.tipoJuicioPadreTrad = :tipoJuicioPadreId)
            """)
    List<TipoJuicioDemandasRecord> findByTipoJuicioPadre(Integer tipoJuicioPadreId);

    @Query(" SELECT tj FROM TipoJuicio tj WHERE tj.id = :idTipoSistema")
    Optional<TipoJuicio> getMateriaAndTipoSistemaById(@Param("idTipoSistema") Integer procedimientoId);

    Optional<TipoJuicio> findByNombreIgnoreCaseAndTipoJuicioPadreOralIsNotNull(String name);

    Optional<TipoJuicio> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
