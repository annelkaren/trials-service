package mx.gob.pjpuebla.trials.litigante;

import java.time.LocalDate;

public interface  LitigantePromocionesLegacyProjection {
    Integer getId();
    String getNumeroExpediente();
    String getNumeroPromocionE();
    String getUsuarioOrigen();
    String getNombreArchivo();
    LocalDate getFechaSubida();
    String getHoraSubida();
    String getRutaArchivo();
    String getJuzgado();
}