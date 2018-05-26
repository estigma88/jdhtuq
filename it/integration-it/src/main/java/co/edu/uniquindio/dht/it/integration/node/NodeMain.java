package co.edu.uniquindio.dht.it.integration.node;

import co.edu.uniquindio.dhash.node.DHashNode;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.storage.StorageNodeFactory;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@Configuration
public class NodeMain {
    private static final Logger logger = Logger
            .getLogger(NodeMain.class);

    public static void main(String[] args) {
        SpringApplication.run(NodeMain.class, args);
    }

    @Bean
    public StorageNode storageNode(StorageNodeFactory storageNodeFactory) throws StorageException {
        String hostname = "Unknown";

        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostname = addr.getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("Error getting hostname", e);
        }

        return storageNodeFactory.createNode(hostname);
    }

    @Bean
    public MessageProcessor messageProcessorIntegrationIT(StorageNode storageNode) {
        return new NodeMessageProcessor((DHashNode) storageNode);
    }

    @Bean
    public MessageServer messageServer(MessageProcessor messageProcessorIntegrationIT) {
        MessageServer messageServer = new MessageServer(messageProcessorIntegrationIT);

        return messageServer;
    }
}
