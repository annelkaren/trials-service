package mx.gob.pjpuebla.trials.config.sendPulse;

import mx.gob.pjpuebla.trials.util.enums.EstadoEnvioCorreo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class EmailGatewayService {

    private final SendPulseClient sendPulseClient;
    private final EmailLogRepository emailLogRepository;

    public EmailGatewayService(SendPulseClient sendPulseClient, EmailLogRepository emailLogRepository) {
        this.sendPulseClient = sendPulseClient;
        this.emailLogRepository = emailLogRepository;
    }

    @Transactional
    public EmailLog sendAndLog(String toEmail, String toName, String subject, String html) {

        String htmlB64 = Base64.getEncoder().encodeToString(html.getBytes(StandardCharsets.UTF_8));

        // TODO: mover estos a config (from email/name)
        SendPulseEmailRequest.Address from = new SendPulseEmailRequest.Address("no-reply@pjpuebla.gob.mx", "TRIALS");
        SendPulseEmailRequest.Address to = new SendPulseEmailRequest.Address(toEmail, toName);
        SendPulseEmailRequest.Email email = new SendPulseEmailRequest.Email(
                htmlB64,
                null,
                subject,
                from,
                List.of(to),
                true);

        String providerMessageId = sendPulseClient.sendEmail(new SendPulseEmailRequest(email));

        EmailLog log = new EmailLog();
        log.setIntentosVerificacion(0);
        log.setUltimaVerificacion(null);
        log.setProximaVerificacion(LocalDateTime.now().plusMinutes(1));
        log.setProvider("SENDPULSE"); 
        log.setProviderMessageId(providerMessageId);
        log.setToEmail(toEmail);
        log.setToName(toName);
        log.setSubject(subject);
        log.setEstado(EstadoEnvioCorreo.PENDIENTE_ENVIO);
        log.setFechaEnvio(LocalDateTime.now());

        return emailLogRepository.save(log);
    }
}