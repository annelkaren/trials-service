package mx.gob.pjpuebla.trials.config.sendPulse;

import java.util.List;

public record SendPulseEmailRequest(
        Email email
) {

    public record Email(
            String html,
            String text,
            String subject,
            Address from,
            List<Address> to
    ) {}

    public record Address(
            String email,
            String name
    ) {}
}