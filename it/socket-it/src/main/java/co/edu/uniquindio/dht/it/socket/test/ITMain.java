package co.edu.uniquindio.dht.it.socket.test;

import co.edu.uniquindio.utils.communication.message.IdGenerator;
import co.edu.uniquindio.utils.communication.message.UUIDGenerator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class ITMain {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ITMain.class)
                .headless(false).run(args);
    }

    @Bean
    public IdGenerator itSequenceGenerator() {
        return new UUIDGenerator();
    }
}
