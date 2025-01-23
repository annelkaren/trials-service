package mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia;

import mx.gob.pjpuebla.trials.litigante.LitiganteExpedienteAudienciaRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AsistenciaAudienciaRepository extends JpaRepository<AsistenciaAudiencia, Integer> {

    AsistenciaAudiencia findByPersonaDocumentoIdAndAudienciaId(
            @Param("personaDocumentoId") Integer personaDocumentoId,
            @Param("audienciaId") Integer audienciaId
    );

    @Query("""
                SELECT new mx.gob.pjpuebla.trials.litigante.LitiganteExpedienteAudienciaRecord(
                c.id,
                c.expediente,
                m.nombre,
                tj.nombre,
                j.nombre,
                a.id,
                a.inicio,
                a.fin
                )
                FROM AsistenciaAudiencia aa
                JOIN aa.personaDocumento pd
                JOIN aa.audiencia a
                JOIN a.carpeta c
                JOIN c.juzgado j
                JOIN j.materia m
                JOIN c.tipoJuicio tj
        WHERE (lower(pd.correoElectronico) = :username
        OR lower(pd.correoNotificacion) = :username)
        """)
    List<LitiganteExpedienteAudienciaRecord> getAllAudicenciasByUser(String username, Pageable pageable);


}
