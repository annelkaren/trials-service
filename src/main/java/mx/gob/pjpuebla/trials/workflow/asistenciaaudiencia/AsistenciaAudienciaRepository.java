package mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface AsistenciaAudienciaRepository extends JpaRepository<AsistenciaAudiencia, Integer> {

    AsistenciaAudiencia findByPersonaDocumentoIdAndAudienciaId(
            @Param("personaDocumentoId") Integer personaDocumentoId,
            @Param("audienciaId") Integer audienciaId
    );

}
