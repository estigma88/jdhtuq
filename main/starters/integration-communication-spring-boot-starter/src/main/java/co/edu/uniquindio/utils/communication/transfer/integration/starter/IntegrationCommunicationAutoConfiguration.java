package co.edu.uniquindio.utils.communication.transfer.network.starter;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;
import co.edu.uniquindio.utils.communication.web.restful.RestfulWebCommunicationManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnClass({RestfulWebCommunicationManagerFactory.class})
@EnableConfigurationProperties(IntegrationCommunicationProperties.class)
public class IntegrationCommunicationAutoConfiguration {
    private final IntegrationCommunicationProperties integrationCommunicationProperties;
    private final RestTemplate restTemplate;
    private final IntegrationFlowContext flowContext;
    private final int webPort;

    public IntegrationCommunicationAutoConfiguration(IntegrationCommunicationProperties integrationCommunicationProperties, RestTemplate restTemplate, IntegrationFlowContext flowContext, @Value("${server.port}") int webPort) {
        this.integrationCommunicationProperties = integrationCommunicationProperties;
        this.restTemplate = restTemplate;
        this.flowContext = flowContext;
        this.webPort = webPort;
    }

    @Bean
    @ConditionalOnMissingBean
    public CommunicationManagerFactory communicationManagerFactory() {
        return new RestfulWebCommunicationManagerFactory(restTemplate, integrationCommunicationProperties.getBaseURL(), integrationCommunicationProperties.getRequestPath(), webPort, new Observable<>(), integrationCommunicationProperties.getInstances(), flowContext);
    }

}
