package mx.gob.pjpuebla.trials.workflow.emailLogs;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import mx.gob.pjpuebla.apis.sendPulse.SendPulseParser;
import mx.gob.pjpuebla.apis.sendPulse.records.SendPulseEmailInfoResponse;

@Service
public class EmailStatusUpdater {

    private SendPulseParser parser;

    public void aplicarInfo(EmailLogs emailLogs, SendPulseEmailInfoResponse info, LocalDateTime ahoraMx) {

        emailLogs.setSmtpAnswerCode(info.smtpAnswerCode());
        emailLogs.setSmtpAnswerSubcode(info.smtpAnswerSubcode());
        emailLogs.setSmtpAnswerCodeExplain(info.smtpAnswerCodeExplain());
        emailLogs.setSmtpAnswerData(info.smtpAnswerData());
        emailLogs.setErrorEnvioDetalle(null);

        if (info.smtpAnswerCode() != null && info.smtpAnswerCode() >= 400) {
            emailLogs.marcarComoNoEntregado();
            return;
        }

        if (info.smtpAnswerCode() != null && info.smtpAnswerCode() >= 200 && info.smtpAnswerCode() < 300) {
            emailLogs.marcarComoEnviadoSiAplica();

            if (emailLogs.getFechaEntrega() == null) {
                LocalDateTime entrega = parser.parsearSendDate(info.sendDate());
                if (entrega != null) {
                    emailLogs.setFechaEntrega(entrega);
                }
            }
        }

         if (info.tracking() != null) {
            emailLogs.setTrackingLinkDetalle(parser.construirTrackingConLinkNormalizado(info.tracking()));
        }

        if (parser.hayEventoOpen(info)) {
            LocalDateTime fechaOpen = parser.extraerPrimeraFechaOpen(info);
            emailLogs.marcarComoLeido(fechaOpen);
        }

        LocalDateTime click = parser.extraerPrimeraFechaClick(info);
        if (click != null && emailLogs.getFechaDescargaVinculo() == null) {
            emailLogs.setFechaDescargaVinculo(click);
        }
    }

}
