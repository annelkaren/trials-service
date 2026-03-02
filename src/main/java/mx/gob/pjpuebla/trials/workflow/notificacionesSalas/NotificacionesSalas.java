package mx.gob.pjpuebla.trials.workflow.notificacionesSalas;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.config.sendPulse.EmailLog;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.Estado;

@Entity
@EntityListeners(AuditListener.class)
@Data
@Table(name = "tbl_notificaciones_salas")
public class NotificacionesSalas implements Serializable, Auditable {
    
    @Id
    @SequenceGenerator(name="idNotificacionesSalas", sequenceName="SEQ_NOTIFICACIONES_SALAS_ID", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="idNotificacionesSalas")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Column(name="s_expediente")
    private String expediente;

    @Column(name = "s_tipo_sala")
    private String tipoSala;

    @Column(name = "s_nombre_destinatario")
    private String nombreDestinatario;

    @Column(name = "s_correo_destinatario")
    private String correoDestinatario;

    @Column(name = "t_fecha_termino")
    private LocalDateTime fechaTermino;

    @Column(name = "s_ruta_archivo")
    private String rutaArchivo;

    @Column(name = "n_estado")
    @Enumerated
    private Estado estado;

    @Column(name = "t_fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "t_fecha_lectura")
    private LocalDateTime fechaLectura;

    @Column(name = "t_fecha_entrega")
    private LocalDateTime fechaEntrega;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "FN_EMAIL_LOG_ID", referencedColumnName = "PN_ID")
    private EmailLog emailLog;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

}
