package co.edu.uniquindio.chord.starter;

import co.edu.uniquindio.chord.node.ChordNodeFactory;
import co.edu.uniquindio.overlay.OverlayNodeFactory;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.structure.CommunicationManagerStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;

@Configuration
@ConditionalOnClass({OverlayNodeFactory.class, ChordNodeFactory.class})
@EnableConfigurationProperties(ChordProperties.class)
public class ChordAutoconfiguration {
    @Autowired
    private ChordProperties chordProperties;

    @Bean
    @ConditionalOnMissingBean
    public OverlayNodeFactory overlayNodeFactory(@Qualifier("communicationManagerStructure") CommunicationManager communicationManager) {
        return new ChordNodeFactory(communicationManager, new HashSet<>());
    }

    @Bean
    @ConditionalOnMissingBean
    public CommunicationManager communicationManagerStructure() {
        return new CommunicationManagerStructure();
    }

}
