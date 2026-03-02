package mx.gob.pjpuebla.trials.config.sendPulse;

import mx.gob.pjpuebla.trials.util.enums.EstadoEnvioCorreo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class EmailGatewayService {

    private final SendPulseClient sendPulseClient;
    private final EmailLogRepository emailLogRepository;
    private final SendPulseProperties sendPulseProperties;

    public EmailGatewayService(SendPulseClient sendPulseClient, EmailLogRepository emailLogRepository,
            SendPulseProperties sendPulseProperties) {
        this.sendPulseClient = sendPulseClient;
        this.emailLogRepository = emailLogRepository;
        this.sendPulseProperties = sendPulseProperties;
    }

    @Transactional
    public EmailLog sendAndLog(String toEmail, String toName, String subject, String html, String attachmentName, byte[] attachmentBytes) {

        String htmlB64 = Base64.getEncoder().encodeToString(html.getBytes(StandardCharsets.UTF_8));

        Map<String, String> attachments = toAttachmentsBinary(attachmentName, attachmentBytes);

        // TODO: mover estos a config (from email/name)
        SendPulseEmailRequest.Address from = new SendPulseEmailRequest.Address("no-reply@pjpuebla.gob.mx", "TRIALS");
        SendPulseEmailRequest.Address to = new SendPulseEmailRequest.Address(toEmail, toName);
        SendPulseEmailRequest.Email email = new SendPulseEmailRequest.Email(
                htmlB64,
                null,
                subject,
                from,
                List.of(to),
                true, 
                attachments);

        String providerMessageId = sendPulseClient.sendEmail(new SendPulseEmailRequest(email));

        EmailLog log = new EmailLog();
        log.setIntentosVerificacion(0);
        log.setUltimaVerificacion(null);
        log.setProximaVerificacion(LocalDateTime.now().plusMinutes(1));
        log.setProvider(sendPulseProperties.provider());
        log.setProviderMessageId(providerMessageId);
        log.setToEmail(toEmail);
        log.setToName(toName);
        log.setSubject(subject);
        log.setEstado(EstadoEnvioCorreo.PENDIENTE_ENVIO);
        log.setFechaEnvio(LocalDateTime.now());

        return emailLogRepository.save(log);
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
}