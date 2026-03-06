package mx.gob.pjpuebla.apis.sendPulse;

import mx.gob.pjpuebla.apis.sendPulse.records.SendPulseEmailRequest;
import mx.gob.pjpuebla.apis.sendPulse.records.SendPulseProperties;
import mx.gob.pjpuebla.trials.util.enums.EstadoEnvioCorreo;
import mx.gob.pjpuebla.trials.workflow.emailLogs.EmailLogs;
import mx.gob.pjpuebla.trials.workflow.emailLogs.EmailLogsRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EmailGatewayService {

    private final SendPulseClient sendPulseClient;
    private final EmailLogsRepository emailLogRepository;
    private final SendPulseProperties sendPulseProperties;

    public EmailGatewayService(SendPulseClient sendPulseClient, EmailLogsRepository emailLogRepository,
            SendPulseProperties sendPulseProperties) {
        this.sendPulseClient = sendPulseClient;
        this.emailLogRepository = emailLogRepository;
        this.sendPulseProperties = sendPulseProperties;
    }

    @Transactional
    public EmailLogs sendAndLog(String toEmail, String toName, String subject, String html, String attachmentName, byte[] attachmentBytes) {

        LocalDateTime now = LocalDateTime.now();

        EmailLogs emailLog = new EmailLogs()
            .setIntentosVerificacion(0)
            .setUltimaVerificacion(null)
            .setProvider(sendPulseProperties.provider())
            .setToEmail(toEmail)
            .setToName(toName)
            .setSubject(subject)
            .setEstado(EstadoEnvioCorreo.PENDIENTE_ENVIO)
            .setFechaEnvio(now);

        String htmlB64 = Base64.getEncoder().encodeToString(html.getBytes(StandardCharsets.UTF_8));

        Map<String, String> attachments = toAttachmentsBinary(attachmentName, attachmentBytes);

        SendPulseEmailRequest.Address from = new SendPulseEmailRequest.Address(sendPulseProperties.fromEmail(), sendPulseProperties.fromName());
        SendPulseEmailRequest.Address to = new SendPulseEmailRequest.Address(toEmail, toName);
        SendPulseEmailRequest.Email email = new SendPulseEmailRequest.Email(
                htmlB64,
                null,
                subject,
                from,
                List.of(to),
                true, 
                attachments);

        try {
            String providerMessageId = sendPulseClient.sendEmail(new SendPulseEmailRequest(email));
            log.info("El id generado es: " + providerMessageId);

            emailLog.setProviderMessageId(providerMessageId);
            emailLog.setEstado(EstadoEnvioCorreo.ENVIADO);
            emailLog.setErrorEnvioDetalle(null);
            return emailLogRepository.save(emailLog);
        } catch (RuntimeException ex) {
            log.info("Ocurrio un error terrible", ex.getMessage());
            emailLog.setEstado(EstadoEnvioCorreo.NO_ENVIADO);
            emailLog.setErrorEnvioDetalle(truncateError(ex.getMessage()));
            emailLogRepository.save(emailLog);
            throw ex;
        }
    }

    public LocalDateTime parseSendPulseToLocal(String value) {
        if (value == null || value.isBlank())
            return null;

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse(value, fmt);

        ZoneId source = ZoneId.of(sendPulseProperties.datesTimeZone()); // "UTC" o "America/Mexico_City"
        return ldt.atZone(source).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    private Map<String, String> toAttachmentsBinary(String filename, byte[] bytes) {
        if (filename == null || filename.isBlank() || bytes == null || bytes.length == 0)
            return null;
        return Map.of(filename, Base64.getEncoder().encodeToString(bytes));
    }

    private String truncateError(String error) {
        if (error == null) {
            return null;
        }
        return error.length() <= 1000 ? error : error.substring(0, 1000);
    }
}
