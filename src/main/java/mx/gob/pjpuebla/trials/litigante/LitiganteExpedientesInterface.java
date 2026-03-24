package mx.gob.pjpuebla.trials.litigante;

public interface LitiganteExpedientesInterface {
    Integer getId();
    String getNumeroExpediente();
    String getMateria();
    String getTipoJuicio();
    String getActorPrincipal();
    String getDemandadoPrincipal();
    String getJuzgado();
    Long getNotificacionesPendientes();
    String getSede();
    String getCu();
}