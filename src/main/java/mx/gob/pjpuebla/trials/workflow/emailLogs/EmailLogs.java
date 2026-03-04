package mx.gob.pjpuebla.trials.workflow.emailLogs;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Type;

import mx.gob.pjpuebla.trials.util.enums.EstadoEnvioCorreo;

@Entity
@Data
@Table(name = "tbl_email_logs")
public class EmailLogs implements Serializable {
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

    @Column(name = "S_ESTADO")
    @Enumerated(EnumType.STRING)
    private EstadoEnvioCorreo estado;

    @Column(name = "N_SMTP_ANSWER_CODE")
    private Integer smtpAnswerCode;

    @Column(name = "S_SMTP_ANSWER_SUBCODE")
    private String smtpAnswerSubcode;

    @Column(name = "S_SMTP_ANSWER_CODE_EXPLAIN")
    private String smtpAnswerCodeExplain;

    @Column(name = "S_SMTP_ANSWER_DATA")
    private String smtpAnswerData;

    @Column(name = "S_ERROR_ENVIO_DETALLE")
    private String errorEnvioDetalle;

    @Column(name = "N_INTENTOS_VERIFICACION")
    private Integer intentosVerificacion = 0;

    @Column(name = "T_PROXIMA_VERIFICACION")
    private LocalDateTime proximaVerificacion;

    @Column(name = "T_FECHA_ENVIO")
    private LocalDateTime fechaEnvio;

    @Column(name = "T_FECHA_ENTREGA")
    private LocalDateTime fechaEntrega;

    @Column(name = "T_FECHA_LECTURA")
    private LocalDateTime fechaLectura;

    @Column(name = "T_FECHA_DESCARGA_VINCULO")
    private LocalDateTime fechaDescargaVinculo;

    @Column(name = "T_ULTIMA_VERIFICACION")
    private LocalDateTime ultimaVerificacion;

    @Type(JsonBinaryType.class)
    @Column(name = "J_TRACKING_LINK_DETALLE", columnDefinition = "jsonb")
    private JsonNode trackingLinkDetalle;

}
