package co.edu.uniquindio.dht.it.socket.node;

import co.edu.uniquindio.dhash.node.DHashNode;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.storage.StorageNodeFactory;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import co.edu.uniquindio.utils.communication.transfer.network.MessageSerialization;
import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@SpringBootApplication
@Configuration
public class NodeMain {
    private static final Logger logger = Logger
            .getLogger(NodeMain.class);

    public static void main(String[] args) {
        new SpringApplicationBuilder(NodeMain.class)
                .headless(false).run(args);
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
    public MessageProcessor messageProcessorSocketIT(StorageNode storageNode) {
        return new NodeMessageProcessor((DHashNode) storageNode);
    }

    @Bean
    public MessageServer messageServer(MessageProcessor messageProcessorSocketIT, MessageSerialization messageSerialization) {
        MessageServer messageServer = new MessageServer(23001, messageSerialization, messageProcessorSocketIT);

        Executors.newSingleThreadExecutor().submit(messageServer);

        return messageServer;
    }
}
