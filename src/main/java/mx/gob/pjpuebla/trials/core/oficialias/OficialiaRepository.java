package mx.gob.pjpuebla.trials.core.oficialias;

import mx.gob.pjpuebla.trials.core.personas.PersonaRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OficialiaRepository extends JpaRepository<Oficialia, Integer> {

    @Query("""
            SELECT
            new mx.gob.pjpuebla.trials.core.oficialias.OficialiaRecord(o.id, o.version, o.nombre, o.responsable, o.estado,
                new mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRecord(t.id, t.nombre),
                new mx.gob.pjpuebla.trials.core.sedes.SedeRecordResponse(s.id, s.nombre, s.estado)
            )
            FROM Oficialia o
            LEFT JOIN o.tipoOficialia t
            LEFT JOIN o.sede s
            WHERE o.id =:id AND o.estado IN :estados""")
    Optional<OficialiaRecord> findByIdAndEstadoIn(Integer id, List<Estado> estados);

}