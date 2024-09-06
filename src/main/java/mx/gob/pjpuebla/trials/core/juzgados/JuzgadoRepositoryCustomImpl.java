package mx.gob.pjpuebla.trials.core.juzgados;

import java.util.Calendar;
import java.util.Date;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;


public class JuzgadoRepositoryCustomImpl implements JuzgadoRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public String generarSecuenciaExpediente(Integer pn_id){
        String nombreSecuencia = "trials.seq_juzgado_expediente_" + pn_id;
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
        String nombreSecuencia = "trials.seq_juzgado_expediente_" + pn_id;
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

        String sqlNumExp = "SELECT nextval('trials.seq_secuencia_juzgado_"+pn_id+"')";

        try{
            Query query = entityManager.createNativeQuery(sqlNumExp);

            calendar.setTime(date);

            numExpediente = query.getSingleResult().toString();

            numExpediente = String.format("%04s/%s", numExpediente, calendar.get(Calendar.YEAR));

            return numExpediente;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }        
    }

    
    public Boolean reiniciarSecuenciaExpediente(Integer pn_id) {
        String sqlSeq = "ALTER SEQUENCE IF EXISTS trials.seq_juzgado_expediente_" + pn_id+" RESTART WITH 1";

        try{
            Query query = entityManager.createNativeQuery(sqlSeq);

            query.executeUpdate();

            return Boolean.TRUE;
        }catch(Exception e){
            return Boolean.FALSE;
        }

        
    }

}
