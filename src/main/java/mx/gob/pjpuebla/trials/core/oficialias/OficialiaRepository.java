package mx.gob.pjpuebla.trials.core.oficialias;

import mx.gob.pjpuebla.trials.core.oficialias.records.OficialiaRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OficialiaRepository extends JpaRepository<Oficialia, Integer> {

        @Query("""
                        SELECT
                        new mx.gob.pjpuebla.trials.core.oficialias.records.OficialiaRecord(o.id, o.version, o.nombre, o.responsable, o.estado,
                            new mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRecord(t.id, t.nombre),
                            new mx.gob.pjpuebla.trials.core.sedes.records.SedeRecordResponse(s.id, s.nombre, s.estado),
                            o.tiposDocumentos
                        )
                        FROM Oficialia o
                        LEFT JOIN o.tipoOficialia t
                        LEFT JOIN o.sede s
                        WHERE o.id =:id AND o.estado IN :estados""")
        Optional<OficialiaRecord> findByIdAndEstadoIn(Integer id, List<Estado> estados);

        @Query("""
                        SELECT o FROM Oficialia o
                        WHERE UPPER(o.tipoOficialia.nombre) = "COMÚN"
                        AND o.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
                        """)
        List<Oficialia> findOficialiaComun();

        @Query("""
                        SELECT new mx.gob.pjpuebla.trials.core.oficialias.OficialiaMateriaRecord(
                            o.id,
                            o.nombre,
                            o.estado,
                            m.nombre,
                            m.id,
                            s.id,
                            t.nombre,
                            t.id,
                            j.nombre,
                            j.id,
                            null
                        )
                        FROM Oficialia o
                        LEFT JOIN o.materias m
                        JOIN o.sede s
                        LEFT JOIN o.juzgados j
                        JOIN o.tipoOficialia t
                        WHERE o.estado IN :estados
                        """)
        Page<OficialiaMateriaRecord> findOficialiaDetails(@Param("estados") List<Estado> estados, Pageable pageable);

        @Query("SELECT new mx.gob.pjpuebla.trials.core.oficialias.OficialiaJuzgadoRecord(" +
                        "j.id, j.nombre) " +
                        "FROM Oficialia o JOIN o.juzgados j " +
                        "WHERE j.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE")
        List<OficialiaJuzgadoRecord> findAllOficialiasWithActiveJuzgados();

        @Query(value = """
                        SELECT DISTINCT o FROM Oficialia o
                        LEFT JOIN o.materias m
                        LEFT JOIN o.juzgados j
                        LEFT JOIN o.tipoOficialia t
                        LEFT JOIN o.sede s
                        WHERE (:key = ''
                            OR LOWER(COALESCE(o.nombre, '')) LIKE LOWER(CONCAT('%', :key, '%'))
                            OR LOWER(COALESCE(m.nombre, '')) LIKE LOWER(CONCAT('%', :key, '%'))
                            OR LOWER(COALESCE(t.nombre, '')) LIKE LOWER(CONCAT('%', :key, '%'))
                            OR LOWER(COALESCE(j.nombre, '')) LIKE LOWER(CONCAT('%', :key, '%')))
                        AND (COALESCE(:nombre, '') = '' OR LOWER(o.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
                        AND (COALESCE(:materia, '') = '' OR LOWER(m.nombre) LIKE LOWER(CONCAT('%', :materia, '%')))
                        AND (COALESCE(:tipo, '') = '' OR LOWER(t.nombre) LIKE LOWER(CONCAT('%', :tipo, '%')))
                        AND (COALESCE(:juzgado, '') = '' OR LOWER(j.nombre) LIKE LOWER(CONCAT('%', :juzgado, '%')))
                        AND o.estado IN :estados
                        """, countQuery = """
                        SELECT count(DISTINCT o) FROM Oficialia o
                        LEFT JOIN o.materias m
                        LEFT JOIN o.sede s
                        LEFT JOIN o.juzgados j
                        LEFT JOIN o.tipoOficialia t
                        WHERE (:key = ''
                            OR LOWER(COALESCE(o.nombre, '')) LIKE LOWER(CONCAT('%', :key, '%'))
                            OR LOWER(COALESCE(m.nombre, '')) LIKE LOWER(CONCAT('%', :key, '%'))
                            OR LOWER(COALESCE(t.nombre, '')) LIKE LOWER(CONCAT('%', :key, '%'))
                            OR LOWER(COALESCE(j.nombre, '')) LIKE LOWER(CONCAT('%', :key, '%')))
                        AND (COALESCE(:nombre, '') = '' OR LOWER(o.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
                        AND (COALESCE(:materia, '') = '' OR LOWER(m.nombre) LIKE LOWER(CONCAT('%', :materia, '%')))
                        AND (COALESCE(:tipo, '') = '' OR LOWER(t.nombre) LIKE LOWER(CONCAT('%', :tipo, '%')))
                        AND (COALESCE(:juzgado, '') = '' OR LOWER(j.nombre) LIKE LOWER(CONCAT('%', :juzgado, '%')))
                        AND o.estado IN :estados
                        """)
        Page<Oficialia> findAllActive(
                        @Param("key") String key,
                        @Param("nombre") String nombre,
                        @Param("materia") String materia,
                        @Param("tipo") String tipo,
                        @Param("juzgado") String juzgado,
                        @Param("estados") List<Estado> estados,
                        Pageable pageable);

        @Query("""
                        SELECT o
                        FROM Oficialia o
                        WHERE o.estado = :estado
                        AND (lower(o.nombre) LIKE %:key%)
                        """)
        List<Oficialia> findAllByEstadoAutocomplete(
                        @Param("estado") Estado estado,
                        @Param("key") String key);

        Optional<Oficialia> findByNombreIgnoreCase(String nombre);

        boolean existsBySedeId(Integer sedeId);

        @Query("""
                        SELECT o
                        FROM Oficialia o
                        JOIN o.juzgados j
                        Where j.id = :juzgadoId
                        and o.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
                        """)
        Optional<Oficialia> findByJuzgadoId(Integer juzgadoId);
}
