package co.edu.uniquindio.dht.it.datastructure;

import org.springframework.context.annotation.Bean;


public class CucumberRoot {
    @Bean
    public World world() {
        return new World();
    }
}
