package mx.gob.pjpuebla.migracion.readers.notificacionAcuerdo;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "notificacion_acuerdo")
public class NotificacionAcuerdoMigracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "clave_actor")
    private String claveActor;

    @Column(name = "clave_acuerdo")
    private Integer claveAcuerdo;

    @Column(name = "tipo_notificacion")
    private String tipoNotificacion;

    @Column(name = "id_domicilio")
    private String idDomicilio;

    @Column(name = "id_correo")
    private Integer idCorreo;

    private String cu;

    @Column(name = "fecha_de_notifi")
    private LocalDateTime fechaNotificacion;

    private String ruta;

    private String status;

    private String notificado;

    private String dias;

    private String motivo;

    private String notas;

}
