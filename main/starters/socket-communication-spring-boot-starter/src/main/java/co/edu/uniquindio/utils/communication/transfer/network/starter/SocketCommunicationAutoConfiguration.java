package co.edu.uniquindio.utils.communication.transfer.network.starter;

import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;
import co.edu.uniquindio.utils.communication.transfer.network.CommunicationManagerTCP;
import co.edu.uniquindio.utils.communication.transfer.network.CommunicationManagerTCPFactory;
import co.edu.uniquindio.utils.communication.transfer.network.jackson.MessageJsonSerialization;
import co.edu.uniquindio.utils.communication.transfer.network.MessageSerialization;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private SocketCommunicationProperties socketCommunicationProperties;

    @Bean
    @ConditionalOnMissingBean
    public CommunicationManagerFactory communicationManagerFactory(MessageSerialization messageSerialization) {
        return new CommunicationManagerTCPFactory(messageSerialization, socketCommunicationProperties.getInstances());
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageSerialization messageSerialization() {
        return new MessageJsonSerialization(new ObjectMapper());
    }

}
