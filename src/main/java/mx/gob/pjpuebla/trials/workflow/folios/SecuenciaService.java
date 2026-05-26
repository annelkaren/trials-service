package mx.gob.pjpuebla.trials.workflow.folios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SecuenciaService {

    @PersistenceContext
    private EntityManager entityManager;

    public Long nextVal(String sequence) {
        try {
            Query query = entityManager.createNativeQuery("SELECT NEXTVAL('" + sequence + "')");
            return ((Number) query.getSingleResult()).longValue();
        } catch (Exception e) {
            log.error("Error al obtener secuencia: " + sequence, e);
            return null;
        }
    }

    public Long getNextValDemanda() {
        return nextVal("SEQ_DEMANDA_FOLIO");
    }

    public Long getNextValExhorto() {
        return nextVal("SEQ_EXHORTO_FOLIO");
    }

    public Long getNextValPromocion() {
        return nextVal("SEQ_PROMOCION_FOLIO");
    }

    public Long getNextValPieza() {
        return nextVal("SEQ_PIEZA_FOLIO");
    }

    public Long getNextValExhortoSalida() {
        return nextVal("SEQ_EXHORTO_SALIDA_FOLIO");
    }

    public Long getNextValApelacion() {
        return nextVal("SEQ_APELACION_FOLIO");
    }
}