package mx.gob.pjpuebla.trials.config.sendPulse;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SendPulseEmailInfoResponse(

        String id,

        @JsonProperty("smtp_answer_code") Integer smtpAnswerCode,

        @JsonProperty("smtp_answer_data") String smtpAnswerData,

        @JsonProperty("smtp_answer_subcode") String smtpAnswerSubcode

) {
}