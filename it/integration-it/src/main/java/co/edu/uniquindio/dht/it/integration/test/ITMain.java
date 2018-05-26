package co.edu.uniquindio.dht.it.integration.test;

import co.edu.uniquindio.utils.communication.message.SequenceGenerator;
import co.edu.uniquindio.utils.communication.message.SequenceGeneratorImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class ITMain {
    public static void main(String[] args) {
        SpringApplication.run(ITMain.class, args);
    }

    @Bean
    public SequenceGenerator itSequenceGenerator() {
        return new SequenceGeneratorImpl();
    }
}
