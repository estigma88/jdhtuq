package co.edu.uniquindio.utils.communication.transfer.network.starter;

import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;
import co.edu.uniquindio.utils.communication.transfer.network.CommunicationManagerTCP;
import co.edu.uniquindio.utils.communication.transfer.network.CommunicationManagerTCPFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({CommunicationManagerTCP.class})
@EnableConfigurationProperties(SocketCommunicationProperties.class)
public class SocketCommunicationAutoConfiguration {
    @Autowired
    private SocketCommunicationProperties dHashProperties;

    @Bean
    @ConditionalOnMissingBean
    public CommunicationManagerFactory communicationManagerFactory() {
        return new CommunicationManagerTCPFactory();
    }

}
