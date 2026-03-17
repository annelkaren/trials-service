package mx.gob.pjpuebla.apis.sendPulse.jobs;

import mx.gob.pjpuebla.apis.sendPulse.SendPulseClient;
import mx.gob.pjpuebla.apis.sendPulse.records.SendPulseEmailInfoResponse;
import mx.gob.pjpuebla.trials.workflow.emailLogs.EmailLogs;
import mx.gob.pjpuebla.trials.workflow.emailLogs.EmailLogsService;
import mx.gob.pjpuebla.trials.workflow.emailLogs.EmailStatusService;
import mx.gob.pjpuebla.trials.workflow.emailLogs.EmailStatusUpdater;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EmailStatusPollingJob {

    private static final ZoneId ZONA_MEXICO = ZoneId.of("America/Mexico_City");
    
    private final EmailLogsService emailLogsService;
    private final EmailStatusService emailStatusService;
    private final EmailStatusUpdater updater;
    private final SendPulseClient sendPulseClient;

    public EmailStatusPollingJob(SendPulseClient sendPulseClient, EmailLogsService emailLogsService, EmailStatusService emailStatusService, EmailStatusUpdater updater) {
        this.sendPulseClient = sendPulseClient;
        this.emailLogsService = emailLogsService;
        this.emailStatusService = emailStatusService;
        this.updater = updater;
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void ejecutarSondeo() {
        LocalDateTime ahoraMx = ZonedDateTime.now(ZONA_MEXICO).toLocalDateTime();
        List<EmailLogs> lote = emailLogsService.getEmailsToVerify();

        if (lote.isEmpty())
            return;

        Map<String, List<EmailLogs>> porProviderId = new HashMap<>();

        for (EmailLogs log : lote) {
            if (emailStatusService.marcarNoLeido(log)) {
                continue;
            }

            if (emailStatusService.estaLeidoFinalizado(log)) {
                continue;
            }

            String providerId = log.getProviderMessageId();
            if (providerId == null || providerId.isBlank()) {
                log.registrarIntentoVerificacion(ahoraMx);
                log.setErrorEnvioDetalle("No se puede consultar SendPulse: providerMessageId vacio.");
            } else {
                porProviderId.computeIfAbsent(providerId, __ -> new ArrayList<>()).add(log);
            }
        }

        if (porProviderId.isEmpty()) {
            return;
        }

        try {
            Map<String, SendPulseEmailInfoResponse> infoPorId = sendPulseClient
                    .getEmailInfoBulk(new ArrayList<>(porProviderId.keySet()));

            for (Map.Entry<String, List<EmailLogs>> entry : porProviderId.entrySet()) {
                String providerId = entry.getKey();
                SendPulseEmailInfoResponse info = infoPorId.get(providerId);

                for (EmailLogs log : entry.getValue()) {
                    if (emailStatusService.marcarNoLeido(log)) {
                        continue;
                    }

                    log.registrarIntentoVerificacion(ahoraMx);

                    if (info == null) {
                        log.setErrorEnvioDetalle("No se obtuvo info de SendPulse para providerMessageId=" + providerId);
                        continue;
                    }

                    updater.aplicarInfo(log, info, ahoraMx);
                }
            }
        } catch (Exception ex) {
            for (List<EmailLogs> grupo : porProviderId.values()) {
                for (EmailLogs log : grupo) {
                    log.registrarIntentoVerificacion(ahoraMx);
                    log.setErrorEnvioDetalle(ex.getMessage());
                }
            }
        }
    }
}
