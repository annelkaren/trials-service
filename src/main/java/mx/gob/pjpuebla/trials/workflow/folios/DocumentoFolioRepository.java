package mx.gob.pjpuebla.trials.workflow.folios;

import mx.gob.pjpuebla.trials.util.enums.TipoCentroTrabajo;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DocumentoFolioRepository extends JpaRepository<DocumentoFolios, Integer> {

    @Query("""
            SELECT df FROM DocumentoFolios df
                WHERE df.centroTrabajoId=:centroTrabajoId
                AND df.tipoCentroTrabajo=:centroTrabajoTipo
                AND df.tipoDocumento=:tipoDocumento
                AND df.year=EXTRACT(YEAR FROM CURRENT_DATE)
            """)
    Optional<DocumentoFolios> findByCentroTrabajoAndTipoDocumento(
            Integer centroTrabajoId, TipoCentroTrabajo centroTrabajoTipo, TipoDocumento tipoDocumento);
}
