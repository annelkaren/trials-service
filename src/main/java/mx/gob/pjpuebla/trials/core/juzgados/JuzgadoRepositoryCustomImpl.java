package mx.gob.pjpuebla.trials.core.juzgados;

import java.util.Calendar;
import java.util.Date;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JuzgadoRepositoryCustomImpl implements JuzgadoRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;
    private String PREFIX_SEQ = "TRIALS.SEQ_JUZGADO_EXPEDIENTE_";

    @Override
    public String generarSecuenciaExpediente(Integer pn_id){
        String nombreSecuencia = PREFIX_SEQ + pn_id;
        String sqlSeq = String.format("CREATE SEQUENCE IF NOT EXISTS %s", nombreSecuencia);

        try{
            Query query = entityManager.createNativeQuery(sqlSeq);

            query.executeUpdate();

            return nombreSecuencia;
        }catch(Exception e){
            return null;
        }
        
    }

    @Override
    public Boolean eliminarSecuenciaExpediente(Integer pn_id){
        String nombreSecuencia = PREFIX_SEQ + pn_id;
        String sqlSeq = String.format("DROP SEQUENCE IF EXISTS %s", nombreSecuencia);

        
        try{
            Query query = entityManager.createNativeQuery(sqlSeq);

            query.executeUpdate();

            return Boolean.TRUE;
        }catch(Exception e){
            return Boolean.FALSE;
        }
    }

    @Override
    public String getNumeroExpediente(Integer pn_id) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        String numExpediente = "";
        String nombreSecuencia = PREFIX_SEQ + pn_id;

        String sqlNumExp = String.format("SELECT LPAD(NEXTVAL('%s')::text,6,'0')", nombreSecuencia);
        System.out.println(sqlNumExp);
        log.info(sqlNumExp);
        try{
            Query query = entityManager.createNativeQuery(sqlNumExp);

            calendar.setTime(date);

            numExpediente = query.getSingleResult().toString();

            numExpediente = String.format("%s/%d", numExpediente, calendar.get(Calendar.YEAR));

            return numExpediente;
        }catch(Exception e){
            log.error("Error: ", e);
            return null;
        }        
    }

    
    public Boolean reiniciarSecuenciaExpediente(Integer pn_id) {
        String nombreSecuencia = PREFIX_SEQ + pn_id;
        String sqlSeq = String.format("ALTER SEQUENCE IF EXISTS %s RESTART WITH 1", nombreSecuencia);

        try{
            Query query = entityManager.createNativeQuery(sqlSeq);

            query.executeUpdate();

            return Boolean.TRUE;
        }catch(Exception e){
            return Boolean.FALSE;
        }
    }

}
