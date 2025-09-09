package mx.gob.pjpuebla.trials.util.ftp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "ftp")
@Validated
@Getter @Setter
public class FtpProps {

    private Map<String, FtpServerProps> servers = new HashMap<>();

    @Getter @Setter
    public static class FtpServerProps {
        private String host;
        private int port = 21;
        private String username;
        @ToString.Exclude
        private String password;
        private boolean passiveMode = true;
        private int connectTimeoutMs = 10000;
        private int dataTimeoutMs = 20000;
    }
}
