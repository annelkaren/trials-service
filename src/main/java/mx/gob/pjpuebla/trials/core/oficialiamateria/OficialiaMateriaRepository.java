package mx.gob.pjpuebla.trials.core.oficialiamateria;


import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepositoryCustom;
import mx.gob.pjpuebla.trials.core.oficialias.OficialiaMateriaRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OficialiaMateriaRepository extends JpaRepository<OficialiaMateria, Integer>, JuzgadoRepositoryCustom {


    @Query("""
        SELECT new mx.gob.pjpuebla.trials.core.oficialias.OficialiaMateriaRecord(
            o.id,
            o.nombre,
            o.estado,
            m.nombre,
            m.id,
            s.id,
            t.nombre,
            j.nombre,
            j.id
        )
        FROM OficialiaMateria om
        JOIN om.oficialia o
        JOIN o.sede s
        JOIN om.materia m
        JOIN o.juzgado j
        JOIN o.tipoOficialia t
        WHERE o.estado IN :estados
        """)
    Page<OficialiaMateriaRecord> findOficialiaDetails(@Param("estados") List<Estado> estados, Pageable pageable);

}
