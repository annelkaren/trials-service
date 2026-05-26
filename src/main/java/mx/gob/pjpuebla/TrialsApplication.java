package mx.gob.pjpuebla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
@EnableScheduling
@SpringBootApplication
public class TrialsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrialsApplication.class, args);
    }

}
