package mx.gob.pjpuebla.trials.config.sendPulse;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.AuditListener;
import mx.gob.pjpuebla.trials.util.Auditable;
import mx.gob.pjpuebla.trials.util.enums.EstadoEnvioCorreo;

@Entity
@EntityListeners(AuditListener.class)
@Data
@Table(name = "tbl_email_log")
public class EmailLog implements Serializable, Auditable {

    @Id
    @SequenceGenerator(name = "idEmailLog", sequenceName = "SEQ_EMAIL_LOG_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idEmailLog")
    @Column(name = "PN_ID", insertable = false, updatable = false)
    private Integer id;

    @Column(name = "S_PROVIDER")
    private String provider;

    @Column(name = "S_PROVIDER_MESSAGE_ID")
    private String providerMessageId;

    @Column(name = "S_TO_EMAIL")
    private String toEmail;

    @Column(name = "S_TO_NAME")
    private String toName;

    @Column(name = "S_SUBJECT")
    private String subject;

    @Column(name = "N_ESTADO")
    private EstadoEnvioCorreo estado;

    @Column(name = "N_SMTP_ANSWER_CODE")
    private Integer smtpAnswerCode;

    @Column(name = "S_SMTP_ANSWER_SUBCODE")
    private String smtpAnswerSubcode;

    @Column(name = "S_SMTP_ANSWER_DATA")
    private String smtpAnswerData;

    @Column(name = "T_FECHA_ENVIO")
    private LocalDateTime fechaEnvio;

    @Column(name = "T_FECHA_ENTREGA")
    private LocalDateTime fechaEntrega;

    @Column(name = "T_FECHA_LECTURA")
    private LocalDateTime fechaLectura;

    @Column(name = "T_ULTIMA_VERIFICACION")
    private LocalDateTime ultimaVerificacion;

    @Accessors(chain = false)
    @Embedded
    private Audit audit;

}