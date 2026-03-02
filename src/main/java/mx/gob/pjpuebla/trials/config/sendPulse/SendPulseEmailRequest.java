package mx.gob.pjpuebla.trials.config.sendPulse;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SendPulseEmailRequest(
        Email email
) {

    public record Email(
            String html,
            String text,
            String subject,
            Address from,
            List<Address> to,
            @JsonProperty("auto_plain_text") Boolean autoPlainText
    ) {}

    public record Address(
            String email,
            String name
    ) {}
}