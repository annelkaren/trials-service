package mx.gob.pjpuebla.trials.util;

import org.springframework.beans.factory.annotation.Value;

public class SendPulseService {
    @Value("${sendpulse.client-id}")
    private String clientId;

    @Value("${sendpulse.client-secret}")
    private String clientSecret;



    public void sendMail(){

    }
}
