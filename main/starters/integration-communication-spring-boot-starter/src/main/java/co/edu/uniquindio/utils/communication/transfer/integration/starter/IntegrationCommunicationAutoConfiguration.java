package co.edu.uniquindio.utils.communication.transfer.integration.starter;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.integration.IntegrationCommunicationManagerFactory;
import co.edu.uniquindio.utils.communication.integration.MessageResponseProcessor;
import co.edu.uniquindio.utils.communication.integration.jackson.*;
import co.edu.uniquindio.utils.communication.integration.sender.ExtendedMessage;
import co.edu.uniquindio.utils.communication.integration.sender.ExtendedMessageTransformer;
import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageType;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnClass({IntegrationCommunicationManagerFactory.class})
@EnableConfigurationProperties(IntegrationCommunicationProperties.class)
public class IntegrationCommunicationAutoConfiguration {
    private final IntegrationCommunicationProperties integrationCommunicationProperties;
    private final IntegrationFlowContext flowContext;
    private final int webPort;

    public IntegrationCommunicationAutoConfiguration(IntegrationCommunicationProperties integrationCommunicationProperties, IntegrationFlowContext flowContext, @Value("${server.port}") int webPort) {
        this.integrationCommunicationProperties = integrationCommunicationProperties;
        this.flowContext = flowContext;
        this.webPort = webPort;
    }

    @Bean
    @ConditionalOnMissingBean
    public CommunicationManagerFactory communicationManagerFactory(Jackson2JsonObjectMapper jackson2JsonObjectMapper, ApplicationContext applicationContext, ExtendedMessageTransformer extendedMessageTransformer, MessageResponseProcessor messageResponseProcessor) {
        return new IntegrationCommunicationManagerFactory(jackson2JsonObjectMapper, webPort, new Observable<>(), integrationCommunicationProperties.getInstances(), flowContext, applicationContext, messageResponseProcessor, extendedMessageTransformer);
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(ObjectMapper customObjectMapper) {
        RestTemplate restTemplate = new RestTemplate();

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(customObjectMapper);

        restTemplate.getMessageConverters().add(0, converter);

        return restTemplate;
    }

    @Bean
    public Jackson2JsonObjectMapper jackson2JsonObjectMapper(ObjectMapper customObjectMapper) {
        return new Jackson2JsonObjectMapper(customObjectMapper);
    }

    @Bean
    public ObjectMapper customObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.addMixIn(ExtendedMessage.class, ExtendedMessageMixIn.class)
                .addMixIn(ExtendedMessage.ExtendedMessageBuilder.class, ExtendedMessageBuilderMixIn.class)
                .addMixIn(MessageType.class, MessageTypeMixIn.class)
                .addMixIn(Address.class, AddressMixIn.class)
                .addMixIn(Message.class, MessageMixIn.class)
                .addMixIn(Message.MessageBuilder.class, MessageBuilderMixIn.class)
                .addMixIn(MessageType.MessageTypeBuilder.class, MessageTypeBuilderMixIn.class)
                .addMixIn(Address.AddressBuilder.class, AddressBuilderMixIn.class);

        return objectMapper;
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageResponseProcessor messageResponseProcessor() {
        return new MessageResponseProcessor();
    }


    @Bean
    @ConditionalOnMissingBean
    public ExtendedMessageTransformer extendedMessageTransformer() {
        return new ExtendedMessageTransformer();
    }

}
