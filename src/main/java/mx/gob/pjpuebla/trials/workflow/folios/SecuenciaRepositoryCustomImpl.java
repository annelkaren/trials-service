package mx.gob.pjpuebla.trials.workflow.folios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecuenciaRepositoryCustomImpl implements SecuenciaRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Long getNextValDemanda() {
        try {
            Query query = entityManager.createNativeQuery("SELECT NEXTVAL('SEQ_DEMANDA_FOLIO')");
            return ((Number) query.getSingleResult()).longValue();  // Cambiado a getSingleResult
        } catch (Exception e) {
            log.error("Error al obtener el siguiente valor de la secuencia de demanda", e);
            return null;
        }
    }

    @Override
    public Long getNextValExhorto() {
        try {
            Query query = entityManager.createNativeQuery("SELECT NEXTVAL('SEQ_EXHORTO_FOLIO')");
            return ((Number) query.getSingleResult()).longValue();  // Cambiado a getSingleResult
        } catch (Exception e) {
            log.error("Error al obtener el siguiente valor de la secuencia de exhorto", e);
            return null;
        }
    }

    @Override
    public Long getNextValPromocion() {
        try {
            Query query = entityManager.createNativeQuery("SELECT NEXTVAL('SEQ_PROMOCION_FOLIO')");
            return ((Number) query.getSingleResult()).longValue();  // Cambiado a getSingleResult
        } catch (Exception e) {
            log.error("Error al obtener el siguiente valor de la secuencia de promoción", e);
            return null;
        }
    }

    @Override
    public Long getNextValPieza() {
        try {
            Query query = entityManager.createNativeQuery("SELECT NEXTVAL('SEQ_PIEZA_FOLIO')");
            return ((Number) query.getSingleResult()).longValue();  // Cambiado a getSingleResult
        } catch (Exception e) {
            log.error("Error al obtener el siguiente valor de la secuencia de pieza", e);
            return null;
        }
    }

    @Override
    public Long getNextValExhortoSalida() {
        try {
            Query query = entityManager.createNativeQuery("SELECT NEXTVAL('SEQ_EXHORTO_SALIDA_FOLIO')");
            return ((Number) query.getSingleResult()).longValue();  // Cambiado a getSingleResult
        } catch (Exception e) {
            log.error("Error al obtener el siguiente valor de la secuencia de exhorto salida", e);
            return null;
        }
    }
}
