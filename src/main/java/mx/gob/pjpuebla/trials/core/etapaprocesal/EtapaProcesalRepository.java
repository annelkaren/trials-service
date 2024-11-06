package mx.gob.pjpuebla.trials.core.etapaprocesal;

import mx.gob.pjpuebla.trials.core.etapaprocesal.record.EtapaProcesalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EtapaProcesalRepository extends JpaRepository<EtapaProcesal, Integer> {

    @Query("""
    SELECT new mx.gob.pjpuebla.trials.core.etapaprocesal.record.EtapaProcesalRecord(
        ep.id, ep.nombre, p.id, p.nombre, ts.id, m.id
    )
    FROM EtapaProcesal ep
    JOIN ep.materia m
    JOIN ep.tipoSistema ts
    LEFT JOIN ep.procedimiento p
    WHERE m.id = :idMateria
    AND ts.id = :idTipoSistema
    AND (:idprocedimiento IS NULL OR p.id = :idprocedimiento)
    """)
    List<EtapaProcesalRecord> getListEtapaProcesalByTipoJuicioAndProcedimiento(
            @Param("idMateria") Integer materiaId,
            @Param("idTipoSistema") Integer tipoSistemaId,
            @Param("idprocedimiento") Integer procedimientoId
    );



}
