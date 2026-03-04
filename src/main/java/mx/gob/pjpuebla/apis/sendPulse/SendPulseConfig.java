package mx.gob.pjpuebla.apis.sendPulse;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import mx.gob.pjpuebla.apis.sendPulse.records.SendPulseProperties;

@Configuration
@EnableConfigurationProperties(SendPulseProperties.class)
public class SendPulseConfig {
}