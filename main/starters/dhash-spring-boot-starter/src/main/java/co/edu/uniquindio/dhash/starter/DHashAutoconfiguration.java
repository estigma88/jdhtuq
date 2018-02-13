package co.edu.uniquindio.dhash.starter;

import co.edu.uniquindio.dhash.node.DHashNodeFactory;
import co.edu.uniquindio.overlay.OverlayNodeFactory;
import co.edu.uniquindio.storage.StorageNodeFactory;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.structure.CommunicationManagerStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({StorageNodeFactory.class, OverlayNodeFactory.class, DHashNodeFactory.class})
@EnableConfigurationProperties(DHashProperties.class)
public class DHashAutoconfiguration {
    @Autowired
    private DHashProperties dHashProperties;

    @Bean
    @ConditionalOnMissingBean
    public StorageNodeFactory storageNodeFactory(OverlayNodeFactory overlayNodeFactory, @Qualifier("communicationManagerStructure") CommunicationManager communicationManager) {
        return new DHashNodeFactory(communicationManager, overlayNodeFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public CommunicationManager communicationManagerStructure() {
        return new CommunicationManagerStructure();
    }

}
