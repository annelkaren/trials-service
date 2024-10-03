package mx.gob.pjpuebla.trials.workflow.carpeta;

import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecordResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarpetaRepository extends JpaRepository<Carpeta, Integer> {

    @Query("""
            SELECT c
            FROM Carpeta c
            WHERE c.expediente = :expediente
            AND c.juzgado.id = :juzgadoId
            """)
    Optional<Carpeta> findByExpedienteAndJuzgadoId(String expediente, Integer juzgadoId);

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecordResponse(
                pd.nombre,
                pd.apellidoPaterno,
                pd.apellidoMaterno,
                pd.pseudonimo,
                pd.tipoPersona,
                pd.rol,
                pd.carpeta.id,
                pd.tipoPartes.nombre,
                pd.tipoPartes.id
            )
            FROM PersonaDocumento pd
            WHERE pd.carpeta.id = :carpetaId
            """)
    List<ApelacionRecordResponse> findPersonaDocumentoByCarpetaId(Integer carpetaId);

}
