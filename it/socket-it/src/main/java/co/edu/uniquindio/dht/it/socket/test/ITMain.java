package co.edu.uniquindio.dht.it.socket.test;

import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
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
    public MessageProcessor messageProcessorIT() {
        return new ITMessageProcessor();
    }

    @Bean
    public CommunicationManager communicationManagerIT(CommunicationManagerFactory communicationManagerFactory, MessageProcessor messageProcessorIT) {
        CommunicationManager communicationManager = communicationManagerFactory.newCommunicationManager("node");
        communicationManager.addMessageProcessor("node", messageProcessorIT);
        return communicationManager;
    }
}
