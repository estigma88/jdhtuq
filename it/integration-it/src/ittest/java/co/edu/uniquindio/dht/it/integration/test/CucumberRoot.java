package co.edu.uniquindio.dht.it.integration.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = ITMain.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration
@Configuration
public class CucumberRoot {
    @Bean
    public World world() {
        return new World();
    }
}
