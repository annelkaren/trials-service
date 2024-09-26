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
    public Long getNextValFolio(String sequenceName) {
        try {
            Query query = entityManager.createNativeQuery("SELECT NEXTVAL('" + sequenceName +"')");
            return ((Number) query.getSingleResult()).longValue();  // Cambiado a getSingleResult
        } catch (Exception e) {
            log.error("Error al obtener el siguiente valor de la secuencia de demanda", e);
            return null;
        }
    }

}
