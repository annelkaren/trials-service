package mx.gob.pjpuebla.trials.workflow.folios;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface SecuenciaRepository {

    @Query(value = "SELECT NEXTVAL('TRIALS.SEQ_DEMANDA_FOLIO')", nativeQuery = true)
    Long getNextValDemanda();

    @Query(value = "SELECT NEXTVAL('TRIALS.SEQ_EXHORTO_FOLIO')", nativeQuery = true)
    Long getNextValExhorto();

    @Query(value = "SELECT NEXTVAL('TRIALS.SEQ_PROMOCION_FOLIO')", nativeQuery = true)
    Long getNextValPromocion();

}