package mx.gob.pjpuebla.trials.workflow.notificacionesSalas;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.emailLogs.EmailLogs;

@Entity
@Data
@EntityListeners(AuditListener.class)
@Table(name = "tbl_notificaciones_salas_destinatarios")
public class NotificacionSalaDestinatario implements Serializable, Auditable {
    @Id
    @SequenceGenerator(name = "idNotificacionSalaDestinatario", sequenceName = "SEQ_NOTIFICACIONES_SALAS_DESTINATARIOS_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idNotificacionSalaDestinatario")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FN_NOTIFICACION_SALA", referencedColumnName = "PN_ID")
    private NotificacionesSalas notificacionSala;

    @Column(name = "S_NOMBRE_DESTINATARIO")
    private String nombreDestinatario;

    @Column(name = "S_CORREO_ELECTRONICO")
    private String correoElectronico;

    @Column(name = "S_TIPO_PARTE")
    private String tipoParte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FN_EMAIL_LOG_ID", referencedColumnName = "PN_ID")
    private EmailLogs emailLog;

    @Column(name = "N_ESTADO")
    @Enumerated
    private Estado estado;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;
}
