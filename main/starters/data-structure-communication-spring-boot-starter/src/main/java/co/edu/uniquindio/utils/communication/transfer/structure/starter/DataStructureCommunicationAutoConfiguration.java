package co.edu.uniquindio.utils.communication.transfer.structure.starter;

import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;
import co.edu.uniquindio.utils.communication.transfer.structure.CommunicationDataStructureFactory;
import co.edu.uniquindio.utils.communication.transfer.structure.CommunicationManagerStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({CommunicationManagerStructure.class})
@EnableConfigurationProperties(DataStructureCommunicationProperties.class)
public class DataStructureCommunicationAutoConfiguration {
    @Autowired
    private DataStructureCommunicationProperties dHashProperties;

    @Bean
    @ConditionalOnMissingBean
    public CommunicationManagerFactory communicationManagerFactory() {
        return new CommunicationDataStructureFactory();
    }

}
