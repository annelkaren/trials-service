package mx.gob.pjpuebla.apis.sendPulse.records;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SendPulseEmailInfoResponse(

        String id,

        @JsonProperty("smtp_answer_code")
        Integer smtpAnswerCode,

        @JsonProperty("smtp_answer_subcode")
        String smtpAnswerSubcode,

        @JsonProperty("smtp_answer_data")
        String smtpAnswerData,

        @JsonProperty("send_date")
        String sendDate,

        Tracking tracking

) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Tracking(
            Integer open,
            Integer click,

            @JsonProperty("client_info")
            List<ActionInfo> clientInfo,

           
            List<ActionInfo> link
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ActionInfo(
            @JsonProperty("action_date")
            String actionDate,
            String browser,
            String os,
            String ip,
            String country,
            String url
    ) {}
}