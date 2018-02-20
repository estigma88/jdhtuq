package co.edu.uniquindio.dhash.starter;

import co.edu.uniquindio.dhash.node.DHashNodeFactory;
import co.edu.uniquindio.dhash.resource.checksum.InputStreamChecksumCalculator;
import co.edu.uniquindio.dhash.resource.checksum.ChecksumeCalculator;
import co.edu.uniquindio.dhash.resource.persistence.FilePersistenceManagerFactory;
import co.edu.uniquindio.dhash.resource.persistence.PersistenceManagerFactory;
import co.edu.uniquindio.dhash.resource.serialization.InputStreamSerializationHandler;
import co.edu.uniquindio.dhash.resource.serialization.SerializationHandler;
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
    public StorageNodeFactory storageNodeFactory(OverlayNodeFactory overlayNodeFactory, @Qualifier("communicationManagerDHash") CommunicationManager communicationManager, SerializationHandler serializationHandler, ChecksumeCalculator checksumeCalculator, PersistenceManagerFactory persistenceManagerFactory) {
        return new DHashNodeFactory(communicationManager, overlayNodeFactory, serializationHandler, checksumeCalculator, persistenceManagerFactory);
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
        return new InputStreamSerializationHandler();
    }

    @Bean
    public ChecksumeCalculator checksumeCalculator() {
        return new InputStreamChecksumCalculator();
    }

    @Bean
    public PersistenceManagerFactory persistenceHandlerFactory() {
        return new FilePersistenceManagerFactory(dHashProperties.getResourceDirectory());
    }
}
