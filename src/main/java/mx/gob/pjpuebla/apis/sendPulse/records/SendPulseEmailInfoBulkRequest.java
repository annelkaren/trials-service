package mx.gob.pjpuebla.apis.sendPulse.records;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SendPulseEmailInfoBulkRequest(
        @JsonProperty("emails")
        List<String> emails
) {
}
