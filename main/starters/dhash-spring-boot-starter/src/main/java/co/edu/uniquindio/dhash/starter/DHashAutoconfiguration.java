package co.edu.uniquindio.dhash.starter;

import co.edu.uniquindio.dhash.node.DHashNodeFactory;
import co.edu.uniquindio.dhash.resource.checksum.BytesChecksumCalculator;
import co.edu.uniquindio.dhash.resource.checksum.ChecksumeCalculator;
import co.edu.uniquindio.dhash.resource.manager.FileResourceManagerFactory;
import co.edu.uniquindio.dhash.resource.manager.ResourceManagerFactory;
import co.edu.uniquindio.dhash.resource.serialization.ObjectSerializationHandler;
import co.edu.uniquindio.dhash.resource.serialization.SerializationHandler;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.overlay.OverlayNodeFactory;
import co.edu.uniquindio.storage.StorageNodeFactory;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.network.CommunicationManagerTCP;
import co.edu.uniquindio.utils.communication.transfer.structure.CommunicationManagerStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnClass({StorageNodeFactory.class, OverlayNodeFactory.class, DHashNodeFactory.class})
@EnableConfigurationProperties(DHashProperties.class)
public class DHashAutoconfiguration {
    @Autowired
    private DHashProperties dHashProperties;

    @Bean
    @ConditionalOnMissingBean
    public StorageNodeFactory storageNodeFactory(OverlayNodeFactory overlayNodeFactory, KeyFactory keyFactory, @Qualifier("communicationManagerDHash") CommunicationManager communicationManager, SerializationHandler serializationHandler, ChecksumeCalculator checksumeCalculator, ResourceManagerFactory resourceManagerFactory) {
        return new DHashNodeFactory(communicationManager, overlayNodeFactory, serializationHandler, checksumeCalculator, resourceManagerFactory, dHashProperties.getReplicationAmount(), keyFactory);
    }

    @Bean("communicationManagerDHash")
    @ConditionalOnMissingBean(name = "communicationManagerDHash")
    @ConditionalOnProperty(prefix = "p2p.dhash",
            name = "communication_type",
            havingValue = "DATA_STRUCTURE")
    public CommunicationManager communicationManagerStructure() {
        CommunicationManagerStructure communication = new CommunicationManagerStructure();

        Map<String, String> params = new HashMap<>();
        params.put("RESPONSE_TIME", "2000");

        communication
                .setCommunicationProperties(params);

        communication.init();

        return communication;
    }

    @Bean("communicationManagerDHash")
    @ConditionalOnMissingBean(name = "communicationManagerDHash")
    @ConditionalOnProperty(prefix = "p2p.dhash",
            name = "communication_type",
            havingValue = "NETWORK")
    public CommunicationManager communicationManagerTCP() {
        return new CommunicationManagerTCP();
    }

    @Bean
    public SerializationHandler serializationHandler() {
        return new ObjectSerializationHandler();
    }

    @Bean
    public ChecksumeCalculator checksumeCalculator() {
        return new BytesChecksumCalculator();
    }

    @Bean
    public ResourceManagerFactory persistenceHandlerFactory() {
        return new FileResourceManagerFactory(dHashProperties.getResourceDirectory());
    }
}
