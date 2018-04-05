package co.edu.uniquindio.dht.it.socket.node;

import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.storage.StorageNodeFactory;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class NodeMain {
    public static void main(String[] args) {
        new SpringApplicationBuilder(NodeMain.class)
                .headless(false).run(args);
    }

    @Bean
    public StorageNode storageNode(StorageNodeFactory storageNodeFactory, @Value("it.socket.nodename") String nodeName) throws StorageException {
        return storageNodeFactory.createNode(nodeName);
    }

    @Bean
    public MessageProcessor messageProcessorSocketIT(StorageNode storageNode) {
        return new NodeMessageProcessor(storageNode);
    }

    @Bean
    public CommunicationManager communicationManagerNode(CommunicationManagerFactory communicationManagerFactory, MessageProcessor messageProcessorSocketIT) {
        CommunicationManager communicationManager = communicationManagerFactory.newCommunicationManager("socketit");
        communicationManager.addMessageProcessor("node", messageProcessorSocketIT);
        return communicationManager;
    }
}
