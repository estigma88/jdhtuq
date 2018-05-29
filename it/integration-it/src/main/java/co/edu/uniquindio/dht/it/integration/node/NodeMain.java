package co.edu.uniquindio.dht.it.integration.node;

import co.edu.uniquindio.dhash.node.DHashNode;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.storage.StorageNodeFactory;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

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

    @EventListener(ApplicationReadyEvent.class)
    public void initStorageNode(ApplicationReadyEvent event) throws StorageException, UnknownHostException {
        NodeMessageProcessor nodeMessageProcessor = (NodeMessageProcessor) event.getApplicationContext().getBean("messageProcessorIntegrationIT");
        StorageNodeFactory storageNodeFactory = event.getApplicationContext().getBean(StorageNodeFactory.class);

        StorageNode storageNode = storageNodeFactory.createNode(InetAddress.getLocalHost().getHostAddress());

        nodeMessageProcessor.setStorageNode((DHashNode) storageNode);
    }

    @Bean
    public MessageProcessor messageProcessorIntegrationIT() {
        return new NodeMessageProcessor();
    }
}
