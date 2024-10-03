package mx.gob.pjpuebla.trials;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
public class TrialsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrialsApplication.class, args);
    }

}
