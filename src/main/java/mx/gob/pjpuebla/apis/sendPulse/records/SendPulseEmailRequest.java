package mx.gob.pjpuebla.apis.sendPulse.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SendPulseEmailRequest(
                Email email) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Email(
                        String html,
                        String text,
                        String subject,
                        Address from,
                        List<Address> to,
                        ReplyTo replyTo,

                        @JsonProperty("auto_plain_text") Boolean autoPlainText,

                        @JsonProperty("attachments_binary") Map<String, String> attachmentsBinary) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Address(
                        String email,
                        String name) {
        }

        public record ReplyTo(
                        String name,
                        String email) {
        }
}