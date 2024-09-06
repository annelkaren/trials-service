package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.util.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JuzgadoRepository extends JpaRepository<Juzgado, Integer>, JuzgadoRepositoryCustom {

    @Query("""
            SELECT 
            new mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecord(f.id, f.version, f.nombre, f.estado, m.id, s.id)
            FROM Juzgado f 
            LEFT JOIN f.materia m
            LEFT JOIN f.sede s
            WHERE f.id =:id AND f.estado IN :estados""")
    Optional<JuzgadoRecord> findByIdAndEstadoIn(Integer id, List<Estado> estados);

}
