package mx.gob.pjpuebla.trials.workflow.folios;

import mx.gob.pjpuebla.trials.core.personas.CentroTrabajoRecord;
import mx.gob.pjpuebla.trials.util.enums.TipoCentroTrabajo;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DocumentoFolioRepository extends JpaRepository<DocumentoFolios, Integer> {

    @Query("""
            SELECT df FROM DocumentoFolios df
                WHERE df.centroTrabajoId=:centroTrabajoRecord.id
                AND df.tipoCentroTrabajo=:centroTrabajoRecord.tipo
                AND df.tipoDocumento=:tipoDocumento
                AND df.year=java.time.Year.now().getValue()
            """)
    Optional<DocumentoFolios> findByCentroTrabajoAndTipoDocumento(
            CentroTrabajoRecord centroTrabajoRecord, TipoDocumento tipoDocumento);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE DocumentoFolios df SET df.folio=:folio WHERE df.id=:documentoFolioId")
    void updateFolio(Integer documentoFolioId, Integer folio);
}
