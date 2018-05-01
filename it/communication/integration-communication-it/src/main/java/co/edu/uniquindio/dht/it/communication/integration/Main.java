package co.edu.uniquindio.dht.it.communication.integration;

import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public CommunicationManager communicationManager(CommunicationManagerFactory communicationManagerFactory){

        CommunicationManager communicationManager = communicationManagerFactory.newCommunicationManager("chord");

        return communicationManager;
    }
}
