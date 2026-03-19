package mx.gob.pjpuebla.trials.workflow.emailLogs;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.apis.sendPulse.SendPulseClient;
import mx.gob.pjpuebla.apis.sendPulse.records.SendPulseEmailInfoResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailVerificacionService {

    private final EmailStatusService emailStatusService;
    private final EmailLogsRepository emailLogsRepo;
    private final SendPulseClient sendPulseClient;
    private final EmailStatusUpdater updater;

    public void verificarYActualizarUnico(EmailLogs log) {
        LocalDateTime ahoraMx = ZonedDateTime.now(ZoneId.of("America/Mexico_City")).toLocalDateTime();

        if (emailStatusService.estaLeidoFinalizado(log)) {
            return;
        }

        String providerId = log.getProviderMessageId();

        if (providerId == null || providerId.isBlank()) {
            log.registrarIntentoVerificacion(ahoraMx);
            log.setErrorEnvioDetalle("No se puede consultar SendPulse: providerMessageId vacío.");
            emailLogsRepo.save(log);
            return;
        }

        try {
            SendPulseEmailInfoResponse info = sendPulseClient.getEmailInfo(providerId);

            log.registrarIntentoVerificacion(ahoraMx);

            if (info == null) {
                log.setErrorEnvioDetalle("No se obtuvo info de SendPulse");
            } else {
                updater.aplicarInfo(log, info, ahoraMx);
            }

            emailStatusService.marcarNoLeido(log);

        } catch (Exception ex) {
            log.registrarIntentoVerificacion(ahoraMx);
            log.setErrorEnvioDetalle("Error verificando estatus: " + ex.getMessage());
        }

        emailLogsRepo.save(log);
    }
}
