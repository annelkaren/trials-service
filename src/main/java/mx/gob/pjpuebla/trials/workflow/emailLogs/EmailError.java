package mx.gob.pjpuebla.trials.workflow.emailLogs;

import mx.gob.pjpuebla.trials.util.enums.EmailErrorType;

public record EmailError(
        EmailErrorType type,
        String message,
        boolean retryable,
        String smtpCode,
        String smtpSubcode) {

    // para catálogo
    public static EmailError of(
            EmailErrorType type,
            String message,
            boolean retryable) {
        return new EmailError(type, message, retryable, null, null);
    }

    // para respuesta final
    public static EmailError withSmtp(
            EmailError base,
            String smtpCode,
            String smtpSubcode) {
        return new EmailError(
                base.type(),
                base.message(),
                base.retryable(),
                smtpCode,
                smtpSubcode);
    }
}