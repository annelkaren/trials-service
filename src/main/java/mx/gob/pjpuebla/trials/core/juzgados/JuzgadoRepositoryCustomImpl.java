package mx.gob.pjpuebla.trials.core.juzgados;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.util.enums.Estado;

@Slf4j
public class JuzgadoRepositoryCustomImpl implements JuzgadoRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;
    private String PREFIX_SEQ = "SEQ_JUZGADO_EXPEDIENTE_";

    @Override
    public String generarSecuenciaExpediente(Integer juzgadoId){
        String nombreSecuencia = PREFIX_SEQ + juzgadoId;
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
    public Boolean eliminarSecuenciaExpediente(Integer juzgadoId){
        String nombreSecuencia = PREFIX_SEQ + juzgadoId;
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
    public String getNumeroExpediente(Integer juzgadoId) {
        LocalDate date = LocalDate.now();
        String numExpediente = "";
        String nombreSecuencia = PREFIX_SEQ + juzgadoId;

        String sqlNumExp = String.format("SELECT LPAD(NEXTVAL('%s')::text,6,'0')", nombreSecuencia);

        log.info(sqlNumExp);

        revisarSecuencia(juzgadoId, date);

        try{
            Query query = entityManager.createNativeQuery(sqlNumExp);

            numExpediente = query.getSingleResult().toString();

            numExpediente = String.format("%s/%d", numExpediente, date.getYear());

            return numExpediente;
        }catch(Exception e){
            log.error("Error: ", e);
            return null;
        }        
    }

    @Override
    public Boolean reiniciarSecuenciasExpedientes(){

        String sqlSeq = String.format("SELECT PN_ID FROM TBL_JUZGADOS WHERE N_ESTADO = %d", Estado.ACTIVE.ordinal());

        try{
            Query query = entityManager.createNativeQuery(sqlSeq);

            @SuppressWarnings("unchecked")
            List<Object> juzgados = query.getResultList();

            for(int i=0;i<juzgados.size();i++){
                Integer id = ((Integer)juzgados.get(i));

                boolean ok = reiniciarSecuenciaExpediente(id);

                if (!ok){
                    return Boolean.FALSE;
                }
            }
        }catch(Exception e){
            log.error("error ->", e.getMessage());
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }


    private Boolean reiniciarSecuenciaExpediente(Integer juzgadoId) {
        String nombreSecuencia = PREFIX_SEQ + juzgadoId;
        String sqlSeq = String.format("ALTER SEQUENCE IF EXISTS %s RESTART WITH 1", nombreSecuencia);

        try{
            Query query = entityManager.createNativeQuery(sqlSeq);

            query.executeUpdate();

            return Boolean.TRUE;
        }catch(Exception e){
            return Boolean.FALSE;
        }
    }

    private String getUltimoExpediente(Integer juzgadoId){
        String ultimoExpediente=null;
        String sql = String.format("SELECT S_EXPEDIENTE FROM TBL_CARPETAS WHERE FN_JUZGADO = %d ORDER BY T_FECHA_ALTA DESC LIMIT 1 ", juzgadoId);

        try{
            Query query = entityManager.createNativeQuery(sql);

            ultimoExpediente = query.getSingleResult().toString();
        }catch(Exception e){
            log.error("error ->", e.getMessage());
        }

        return ultimoExpediente;
    }

    public Boolean revisarSecuencia(Integer juzgadoId, LocalDate dateReview){
        //LocalDate date = LocalDate.now();

        if (dateReview.getMonthValue()>1 && dateReview.getDayOfMonth()>=15){
            return Boolean.FALSE;
        }

        String tmp = getUltimoExpediente(juzgadoId);

        if (tmp != null && !tmp.endsWith(Integer.toString(dateReview.getYear()))){
            reiniciarSecuenciaExpediente(juzgadoId);

            return Boolean.TRUE;
        }

        return Boolean.FALSE;
        
    }


}
