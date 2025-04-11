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
                new mx.gob.pjpuebla.trials.core.sedes.records.SedeRecordResponse(s.id, s.nombre, s.estado)
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
            j.id
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

    @Query("""
     Select o FROM Oficialia o
     LEFT JOIN o.materias m
     LEFT JOIN o.juzgados j
     LEFT JOIN o.tipoOficialia t
     WHERE (
                 :searchTerm IS NULL OR
                LOWER(TRANSLATE(o.nombre, 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) LIKE LOWER(TRANSLATE(CONCAT('%', :searchTerm, '%'), 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) OR
                LOWER(TRANSLATE(m.nombre, 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) LIKE LOWER(TRANSLATE(CONCAT('%', :searchTerm, '%'), 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) OR
                LOWER(TRANSLATE(o.tipoOficialia.nombre, 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) LIKE LOWER(TRANSLATE(CONCAT('%', :searchTerm, '%'), 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) OR
                LOWER(TRANSLATE(j.nombre, 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) LIKE LOWER(TRANSLATE(CONCAT('%', :searchTerm, '%'), 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN'))
     )
     AND (
         o.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
         OR o.estado = mx.gob.pjpuebla.trials.util.enums.Estado.INACTIVE
     )
 """)
    Page<Oficialia> findAllActive(@Param("searchTerm") String searchTerm, Pageable pageable);


    @Query("""
        SELECT o
        FROM Oficialia o
        WHERE o.estado = :estado
        AND (lower(o.nombre) LIKE %:key%)
        """)
    List<Oficialia> findAllByEstadoAutocomplete(
            @Param("estado") Estado estado,
            @Param("key") String key
    );

    Optional<Oficialia> findByNombreIgnoreCase(String nombre);
    
    boolean existsBySedeId(Integer sedeId);
}
