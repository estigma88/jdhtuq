package co.edu.uniquindio.dhash.starter;

import co.edu.uniquindio.dhash.node.DHashNodeFactory;
import co.edu.uniquindio.dhash.resource.checksum.ChecksumCalculator;
import co.edu.uniquindio.dhash.resource.checksum.ChecksumInputStreamCalculator;
import co.edu.uniquindio.dhash.resource.manager.FileResourceManagerFactory;
import co.edu.uniquindio.dhash.resource.manager.ResourceManagerFactory;
import co.edu.uniquindio.dhash.resource.serialization.ObjectMapperSerializationHandler;
import co.edu.uniquindio.dhash.resource.serialization.SerializationHandler;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.overlay.OverlayNodeFactory;
import co.edu.uniquindio.storage.StorageNodeFactory;
import co.edu.uniquindio.utils.communication.message.IdGenerator;
import co.edu.uniquindio.utils.communication.message.UUIDGenerator;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

@Configuration
@ConditionalOnClass({StorageNodeFactory.class, OverlayNodeFactory.class, DHashNodeFactory.class})
@EnableConfigurationProperties(DHashProperties.class)
public class DHashAutoConfiguration {
    @Autowired
    private DHashProperties dHashProperties;

    @Bean
    @ConditionalOnMissingBean
    public StorageNodeFactory storageNodeFactory(OverlayNodeFactory overlayNodeFactory, KeyFactory keyFactory, CommunicationManager communicationManagerDHash, SerializationHandler serializationHandler, ChecksumCalculator checksumeCalculator, ResourceManagerFactory resourceManagerFactory, IdGenerator dhashSequenceGenerator) {
        return new DHashNodeFactory(communicationManagerDHash, overlayNodeFactory, serializationHandler, checksumeCalculator, resourceManagerFactory, dHashProperties.getReplicationAmount(), keyFactory, dhashSequenceGenerator, Executors.newCachedThreadPool());
    }

    @Bean
    @ConditionalOnMissingBean(name = {"communicationManagerDHash"})
    public CommunicationManager communicationManagerDHash(CommunicationManagerFactory communicationManagerFactory) {
        return communicationManagerFactory.newCommunicationManager("dhash");
    }

    @Bean
    @ConditionalOnMissingBean
    public SerializationHandler serializationHandler() {
        return new ObjectMapperSerializationHandler(new ObjectMapper());
    }

    @Bean
    @ConditionalOnMissingBean
    public ChecksumCalculator checksumeCalculator() {
        return new ChecksumInputStreamCalculator();
    }

    @Bean
    @ConditionalOnMissingBean
    public IdGenerator dhashSequenceGenerator() {
        return new UUIDGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public ResourceManagerFactory persistenceHandlerFactory() {
        return new FileResourceManagerFactory(dHashProperties.getResourceDirectory(), dHashProperties.getBufferSize());
    }
}
