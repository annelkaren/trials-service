package mx.gob.pjpuebla.trials.workflow.notificaciones.DTO;

import lombok.Data;

@Data
public class NotificacionDto {
    private String calle;
    private String ciudad;
    private String codigoPostal;
    private String colonia;
    private String correo;
    private String estadoRepublica;
    private String exterior;
    private Long idDomicilio;
    private String interior;
    private String latitud;
    private String longitud;
    private Integer metodo;
    private String municipio;
    private Integer personId;
    private Boolean usarCorreoRegistrado;
}
