package co.edu.uniquindio.dht.it.socket.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = ITMain.class)
@ContextConfiguration
@Configuration
public class CucumberRoot {
    @Bean
    public World world() {
        return new World();
    }
}
