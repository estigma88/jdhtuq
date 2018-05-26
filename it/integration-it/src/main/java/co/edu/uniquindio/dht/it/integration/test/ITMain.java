package co.edu.uniquindio.dht.it.integration.test;

import co.edu.uniquindio.utils.communication.message.SequenceGenerator;
import co.edu.uniquindio.utils.communication.message.SequenceGeneratorImpl;
import co.edu.uniquindio.utils.communication.transfer.network.MessageSerialization;
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
    public SequenceGenerator itSequenceGenerator() {
        return new SequenceGeneratorImpl();
    }
}
